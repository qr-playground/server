package com.example.demo.global.error;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.global.error.exception.CustomException;
import com.example.demo.global.error.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // 커스텀 예외 처리
        @ExceptionHandler(CustomException.class)
        public ResponseEntity<ErrorResponse> handleCustom(CustomException ex, HttpServletRequest request) {
                ErrorCode code = ex.getErrorCode();

                log.error("커스텀 예외 발생: [{}] {}", code, ex.getMessage());

                ErrorResponse response = ErrorResponse.of(
                                code,
                                request.getRequestURI());

                return ResponseEntity.status(code.getStatus()).body(response);
        }

        // 토큰 인증 실패
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex,
                        HttpServletRequest request) {
                log.error("토큰 인증 실패: {}", ex.getMessage());

                ErrorCode code = ex.getErrorCode();

                ErrorResponse response = ErrorResponse.of(code, request.getRequestURI());

                return ResponseEntity.status(code.getStatus()).body(response);
        }

        // 400 Bad Request 예외 처리
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                ErrorCode code = ErrorCode.COMMON_INVALID_INPUT_VALUE;

                List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> new ErrorResponse.FieldError(
                                                err.getField(),
                                                err.getDefaultMessage()))
                                .collect(Collectors.toList());

                log.error("유효성 검사 실패: {}", fieldErrors);
                ErrorResponse response = ErrorResponse.of(code, request.getRequestURI(), fieldErrors);

                return ResponseEntity
                                .status(code.getStatus())
                                .body(response);
        }

        // DB 제약조건 위반 (NULL, UNIQUE, FK 등)
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex,
                        HttpServletRequest request) {
                log.error("DB 무결성 제약 위반: {}", ex.getMessage());

                ErrorCode code = ErrorCode.COMMON_QUERY_FAILED;
                ErrorResponse response = ErrorResponse.of(code, request.getRequestURI());
                return ResponseEntity
                                .status(code.getStatus())
                                .body(response);
        }

        // 기타 모든 DB 접근 예외
        @ExceptionHandler(DataAccessException.class)
        public ResponseEntity<ErrorResponse> handleDataAccess(
                        DataAccessException ex,
                        HttpServletRequest request) {
                log.error("DB 접근 중 예외 발생: {}", ex.getMessage());

                ErrorCode code = ErrorCode.COMMON_DB_CONNECTION_FAILED;
                ErrorResponse response = ErrorResponse.of(code, request.getRequestURI());
                return ResponseEntity
                                .status(code.getStatus())
                                .body(response);
        }

        // 처리되지 않은 모든 예외를 처리하는 기본 핸들러
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception e, HttpServletRequest request) {
                ErrorResponse response = ErrorResponse.of(
                                ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                                request.getRequestURI());
                return ResponseEntity
                                .status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus())
                                .body(response);
        }
}
