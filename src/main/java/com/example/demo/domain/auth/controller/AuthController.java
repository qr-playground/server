package com.example.demo.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.auth.dto.AuthDto;
import com.example.demo.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * @param AuthDto.Signup 회원가입 요청 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.Response> signup(@Valid @RequestBody AuthDto.Signup requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(requestDto));
    }

    /**
     * 로그인 API
     * 
     * @param AuthDto.Login 로그인 요청 정보
     * @return 토큰 정보
     */
    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(@Valid @RequestBody AuthDto.Login requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDto.Response> refresh(@Valid @RequestBody AuthDto.Refresh requestDto) {
        return ResponseEntity.ok(authService.refresh(requestDto));
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<Void> sendVerificationCode(@Valid @RequestBody AuthDto.SendVerificationCode requestDto) {
        authService.sendVerificationCode(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-verification-code")
    public ResponseEntity<Void> verifyVerificationCode(@Valid @RequestBody AuthDto.VerifyVerificationCode requestDto) {
        authService.verifyVerificationCode(requestDto);
        return ResponseEntity.ok().build();
    }
}
