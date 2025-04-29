package com.example.demo.domain.auth.service;

import com.example.demo.domain.auth.dto.LoginRequestDto;
import com.example.demo.domain.auth.dto.TokenDto;
import com.example.demo.domain.user.dto.UserDto;
import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public UserDto.Response signup(UserDto.Create requestDto) {
        // 이미 가입된 사용자인지 확인
        if (userRepository.findByPhoneNumber(requestDto.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("이미 가입된 사용자입니다");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 엔티티 생성
        User user = User.builder()
                .phoneNumber(requestDto.getPhoneNumber())
                .password(encodedPassword)
                .role(Role.USER)
                .build();

        // 저장 및 응답 변환
        return UserDto.Response.fromEntity(userRepository.save(user));
    }

    /**
     * 로그인 및 토큰 발급
     */
    @Transactional
    public TokenDto login(LoginRequestDto requestDto) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                requestDto.getPhoneNumber(), requestDto.getPassword());

        // 실제 인증 진행 (CustomUserDetailsService.loadUserByUsername 메서드가 실행됨)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication.getName());

        return TokenDto.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiresIn())
                .build();
    }
}