package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.AuthDto;
import com.example.demo.domain.user.service.AuthService;
import com.example.demo.global.dto.ErrorResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "회원가입",
        description = "새로운 사용자를 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = AuthDto.Response.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "409", description = "로그인 ID 중복 ", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 오류 발생", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthDto.Response> signUpUser(@Valid @RequestBody AuthDto.SignUp signUpRequest) {
        AuthDto.Response response = authService.signUpUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
        summary = "로그인",
        description = "사용자가 로그인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = AuthDto.Response.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "인증 정보가 일치하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "500", description = "예상치 못한 서버 오류 발생", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> loginUser(@Valid @RequestBody AuthDto.Login loginRequest) {
        AuthDto.Response response = authService.loginUser(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}