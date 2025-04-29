package com.example.demo.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.global.error.exception.CustomException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅을 위한 Lombok 어노테이션
@RestControllerAdvice // 전역 예외 처리를 위한 핵심 어노테이션
public class GlobalExceptionHandler {

        // 여기에 다양한 예외 처리 메서드들이 추가될 예정

        /**
         * 처리되지 않은 모든 예외를 처리하는 기본 핸들러
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception e, HttpServletRequest request) {

                // 오류 로깅
                log.error("처리되지 않은 예외 발생", e);

                // 에러 응답 생성
                ErrorResponse response = ErrorResponse.of(
                                ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                                request.getRequestURI());

                // 응답 반환
                return ResponseEntity
                                .status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus())
                                .body(response);
        }

        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustom(CustomException ex, HttpServletRequest request) {
                ErrorCode code = ex.getErrorCode();

                ErrorResponse response = ErrorResponse.of(
                                code,
                                request.getRequestURI());

                return ResponseEntity.status(code.getStatus()).body(response);
        }

}
