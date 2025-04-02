package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.AuthDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.util.JwtTokenProvider;
import com.example.demo.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthDto.Response signUpUser(AuthDto.SignUp createUser) {
        User user = createUser.toEntity(passwordEncoder);
        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.createToken(savedUser.getLoginId());
        return AuthDto.Response.fromEntity(savedUser, token);
    }

    public AuthDto.Response loginUser(AuthDto.Login loginRequest) {
        User user = userRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new ResourceNotFoundException("인증 정보가 일치하지 않음. ID: " + loginRequest.getLoginId()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("인증 정보가 일치하지 않음. ID: " + loginRequest.getLoginId());
        }

        String token = jwtTokenProvider.createToken(user.getLoginId());
        return AuthDto.Response.fromEntity(user, token);
    }
}