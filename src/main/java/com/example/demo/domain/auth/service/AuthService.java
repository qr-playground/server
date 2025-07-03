package com.example.demo.domain.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.auth.dto.AuthDto;
import com.example.demo.domain.auth.dto.TokenDto;
import com.example.demo.domain.auth.entity.RefreshToken;
import com.example.demo.domain.auth.repository.RefreshTokenRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;
import com.example.demo.global.security.jwt.JwtProperties;
import com.example.demo.global.security.jwt.JwtTokenProvider;
import com.example.demo.global.security.jwt.JwtTokenStatus;
import com.example.demo.global.security.user.CustomUserDetails;
import com.example.demo.global.service.SmsService;
import com.github.benmanes.caffeine.cache.Cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final SmsService smsService;
    private final Cache<String, Boolean> verifiedPhoneNumberCache;

    /**
     * 회원가입
     */
    // ! TODO: 회원가입 시 핸드폰 번호 문자 인증
    @Transactional
    public AuthDto.Response signup(AuthDto.Signup requestDto) {
        // verifiedPhoneNumberCache 에 인증된 핸드폰 번호가 없으면 예외 발생 
        if (verifiedPhoneNumberCache.getIfPresent(requestDto.getPhoneNumber()) != true) {
            throw new CustomException(ErrorCode.AUTH_NOT_VERIFIED_PHONE_NUMBER);
        }
        verifiedPhoneNumberCache.invalidate(requestDto.getPhoneNumber());

        Optional<User> user = userService.getUserByPhoneNumber(requestDto.getPhoneNumber());

        // 이미 가입된 사용자인지 확인
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.AUTH_DUPLICATE_USER);
        }

        // 사용자 엔티티 생성
        User newUser = requestDto.toEntity(passwordEncoder.encode(requestDto.getPassword()));

        // 저장 및 응답 변환
        return AuthDto.Response.fromEntity(userService.createUser(newUser));
    }

    /**
     * 로그인 및 토큰 발급
     */
    @Transactional // ! TODO: RTR 도입, RTR 도입 시 트랜잭션 처리 필요
    public AuthDto.Response login(AuthDto.Login requestDto) {

        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate( // ← 빈 사용
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getPhoneNumber(),
                            requestDto.getPassword()));

        } catch (BadCredentialsException e) {
            // 비밀번호 불일치 또는 유저정보 없음
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.AUTH_NOT_FOUND_USER);
        }

        // 2) 인증 성공 후, principal 에 담긴 CustomUserDetails 에서 User 꺼내기
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 3) JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getPhoneNumber());

        // 4) 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = new RefreshToken(user, refreshToken,
                jwtProperties.getRefreshTokenValidityInSeconds());
        refreshTokenRepository.save(refreshTokenEntity);

        TokenDto tokenDto = TokenDto.builder()
                .grantType(jwtProperties.getGrantType())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtProperties.getTokenValidityInSeconds())
                .build();

        // 4) 응답으로 User + TokenInfo 반환
        return AuthDto.Response.fromEntity(user, tokenDto);
    }

    // TODO: RTR 도입 시 트랜잭션 처리 필요
    @Transactional(readOnly = true)
    public AuthDto.Response refresh(AuthDto.Refresh requestDto) {

        JwtTokenStatus status = jwtTokenProvider.validateToken(requestDto.getRefreshToken());
        // 올바른 리프레시 토큰만 통과, 만료된 리프레시 토큰 -> status != JwtTokenStatus.EXPIRED
        // 재로그인 요청
        if (status != JwtTokenStatus.VALID) {
            throw new CustomException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        // 존재하지 않는 리프레시 토큰 -> RTR 도입시 주석 해제
        // Optional<RefreshToken> refreshToken = refreshTokenRepository
        // .findByRefreshTokenAndIsRevokedFalse(requestDto.getRefreshToken());
        // if (refreshToken.isEmpty()) {
        // throw new CustomException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        // }

        String phoneNumber = jwtTokenProvider.getUsernameFromToken(requestDto.getRefreshToken());

        Optional<User> user = userService.getUserByPhoneNumber(phoneNumber);

        if (user.isEmpty()) {
            throw new CustomException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.get().getRole().name()));
        CustomUserDetails userDetails = new CustomUserDetails(user.get());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String accessToken = jwtTokenProvider.createToken(authentication);
        return AuthDto.Response.fromEntity(TokenDto.builder()
                .grantType(jwtProperties.getGrantType())
                .accessToken(accessToken)
                .refreshToken(requestDto.getRefreshToken())
                .accessTokenExpiresIn(jwtProperties.getTokenValidityInSeconds())
                .build());
    }
    
    public void sendVerificationCode(AuthDto.SendVerificationCode requestDto) {
        if (smsService.sendVerificationCodeSms(requestDto.getPhoneNumber()) != true) {
            throw new CustomException(ErrorCode.AUTH_SEND_VERIFICATION_CODE_FAILED);
        }
    }

    public void verifyVerificationCode(AuthDto.VerifyVerificationCode requestDto) {
        if (smsService.verifyCode(requestDto.getPhoneNumber(), requestDto.getVerificationCode()) != true) {
            throw new CustomException(ErrorCode.AUTH_VERIFY_VERIFICATION_CODE_FAILED);
        }
    }

    public AuthDto.Response getDeviceId() {
        return AuthDto.Response.fromEntity(jwtTokenProvider.createDeviceIdToken());
    }
}