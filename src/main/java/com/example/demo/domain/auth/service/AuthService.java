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
import com.example.demo.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

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
        return AuthDto.Response.of(userRepository.save(newUser));
    }

    /**
     * 로그인 및 토큰 발급 
     */
    @Transactional // ! TODO: readonly 처리
    public AuthDto.Response login(AuthDto.Login requestDto) {
        // 사용자 존재 여부 먼저 확인 (CustomException 사용)
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_NOT_FOUND_USER));

        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                requestDto.getPhoneNumber(), requestDto.getPassword());

        try {
            // 실제 인증 진행 (비밀번호 검증)
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // JWT 토큰 생성
            String accessToken = jwtTokenProvider.createToken(authentication);
            String refreshToken = jwtTokenProvider.createRefreshToken(authentication.getName());

            TokenDto tokenDto = TokenDto.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiresIn())
                    .build();

            return AuthDto.Response.of(user, tokenDto);
        } catch (BadCredentialsException e) {
            // 비밀번호 불일치 예외
            throw new CustomException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
    }
}