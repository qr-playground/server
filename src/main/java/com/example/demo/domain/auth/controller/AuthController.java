package com.example.demo.domain.auth.controller;

import com.example.demo.domain.auth.dto.LoginRequestDto;
import com.example.demo.domain.auth.dto.TokenDto;
import com.example.demo.domain.auth.service.AuthService;
import com.example.demo.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * @param requestDto 회원가입 요청 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto.Response> signup(@Valid @RequestBody UserDto.Create requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(requestDto));
    }

    /**
     * 로그인 API
     * 
     * @param requestDto 로그인 요청 정보
     * @return 토큰 정보
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }
}