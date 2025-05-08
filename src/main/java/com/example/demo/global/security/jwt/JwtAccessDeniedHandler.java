package com.example.demo.global.security.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
 * JWT 인증은 됐으나 권한이 없는 경우 처리를 담당하는 클래스
 * Spring Security의 AccessDeniedHandler를 구현하여 권한이 없는 사용자의 요청에 대한 처리를 정의
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("권한 없음: {}", accessDeniedException.getMessage());

        // 접근 거부 응답 설정
        response.setStatus(ErrorCode.COMMON_ACCESS_DENIED.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 공통 에러 응답 형식 사용
        ErrorResponse errorResponse = ErrorResponse.of(
                ErrorCode.COMMON_ACCESS_DENIED,
                request.getRequestURI());

        // JSON 형태로 변환하여 응답
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}