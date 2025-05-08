package com.example.demo.domain.auth.service;

import java.util.Optional;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.auth.dto.AuthDto;
import com.example.demo.domain.auth.dto.TokenDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;
import com.example.demo.global.security.jwt.JwtProperties;
import com.example.demo.global.security.jwt.JwtTokenProvider;
import com.example.demo.global.security.user.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProperties jwtProperties;

    /**
     * 회원가입
     */
    // ! TODO: 회원가입 시 핸드폰 번호 문자 인증
    @Transactional
    public AuthDto.Response signup(AuthDto.Signup requestDto) {

        Optional<User> user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber());

        // 이미 가입된 사용자인지 확인
        if (user.isPresent()) {
            throw new CustomException(ErrorCode.AUTH_DUPLICATE_USER);
        }

        // 사용자 엔티티 생성
        User newUser = requestDto.toEntity(passwordEncoder.encode(requestDto.getPassword()));

        // 저장 및 응답 변환
        return AuthDto.Response.fromEntity(userRepository.save(newUser));
    }

    /**
     * 로그인 및 토큰 발급
     */
    @Transactional(readOnly = true)
    public AuthDto.Response login(AuthDto.Login requestDto) {
        try {
            // 1) 인증 시도: 내부적으로 UserDetailsService.loadUserByUsername → 한 번만 쿼리
            Authentication authentication = authenticationManagerBuilder.getObject()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    requestDto.getPhoneNumber(),
                                    requestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 2) 인증 성공 후, principal 에 담긴 CustomUserDetails 에서 User 꺼내기
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            // 3) JWT 토큰 생성
            String accessToken = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getPhoneNumber());

            TokenDto tokenDto = TokenDto.builder()
                    .grantType(jwtProperties.getGrantType())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiresIn(jwtProperties.getTokenValidityInSeconds())
                    .build();

            // 4) 응답으로 User + TokenInfo 반환
            return AuthDto.Response.fromEntity(user, tokenDto);
        } catch (BadCredentialsException e) {
            // 비밀번호 불일치
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }
}