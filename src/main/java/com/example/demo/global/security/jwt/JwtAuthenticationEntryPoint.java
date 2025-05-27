package com.example.demo.global.security.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 인증 실패 시 처리를 담당하는 클래스
 * Spring Security의 AuthenticationEntryPoint를 구현하여 인증되지 않은 사용자의 요청에 대한 처리를 정의
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        log.error("인증 실패: {}", authException.getMessage());

        // 인증 실패 응답 설정
        response.setStatus(ErrorCode.COMMON_UNAUTHORIZED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 공통 에러 응답 형식 사용
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.COMMON_UNAUTHORIZED,
                request.getRequestURI());

        // JSON 형태로 변환하여 응답
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}