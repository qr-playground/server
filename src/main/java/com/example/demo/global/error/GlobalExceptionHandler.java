package com.example.demo.global.error;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.demo.global.error.exception.CustomException;
import com.example.demo.global.error.exception.RateLimitException;
import com.example.demo.global.error.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        // Rate Limit 예외 처리 (CustomException보다 먼저 처리)
        @ExceptionHandler(RateLimitException.class)
        public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitException ex, HttpServletRequest request) {
                ErrorCode code = ex.getErrorCode();

                log.error("Rate Limit 예외 발생: [{}] {} - 재시도 가능 시간: {}초",
                                code, ex.getMessage(), ex.getRetryAfterSeconds());

                // Rate Limit 전용 응답 생성
                RateLimitErrorResponse response = RateLimitErrorResponse.of(
                                code,
                                request.getRequestURI(),
                                ex.getRetryAfterSeconds());

                return ResponseEntity.status(code.getStatus())
                                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(ex.getRetryAfterSeconds()))
                                .body(response);
        }

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

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                        HttpServletRequest request) {
                log.error("NoResourceFoundException 예외 발생: {}", ex.getMessage());
                ErrorCode code = ErrorCode.COMMON_RESOURCE_NOT_FOUND;
                ErrorResponse response = ErrorResponse.of(code, request.getRequestURI());
                return ResponseEntity.status(code.getStatus()).body(response);
        }

        // SSE 스트림에서 클라이언트 단절(소켓 끊김) 시 발생하는 비동기 요청 사용 불가 예외는 하향 처리
        @ExceptionHandler(AsyncRequestNotUsableException.class)
        public ResponseEntity<?> handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex,
                        HttpServletRequest request) {
                if (isSseRequest(request) && isClientDisconnect(ex)) {
                        return ResponseEntity.noContent().build();
                }

                log.error("AsyncRequestNotUsableException: {}", ex.getMessage());
                ErrorResponse response = ErrorResponse.of(ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                                request.getRequestURI());
                return ResponseEntity.status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus()).body(response);
        }

        // SSE 환경의 Broken pipe 등 I/O 예외는 하향 로그 처리
        @ExceptionHandler(IOException.class)
        public ResponseEntity<?> handleIoException(IOException ex, HttpServletRequest request) {
                if (isSseRequest(request) && isClientDisconnect(ex)) {
                        return ResponseEntity.noContent().build();
                }

                log.error("IO 예외 발생: {}", ex.getMessage());
                ErrorResponse response = ErrorResponse.of(ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                                request.getRequestURI());
                return ResponseEntity.status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus()).body(response);
        }

        // 처리되지 않은 모든 예외를 처리하는 기본 핸들러
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception e, HttpServletRequest request) {

                log.error("처리되지 않은 예외 발생: {}", e);

                ErrorResponse response = ErrorResponse.of(
                                ErrorCode.COMMON_INTERNAL_SERVER_ERROR,
                                request.getRequestURI());
                return ResponseEntity
                                .status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus())
                                .body(response);
        }

        private boolean isSseRequest(HttpServletRequest request) {
                String accept = request.getHeader("Accept");
                String contentType = request.getContentType();
                String uri = request.getRequestURI();
                return (accept != null && accept.contains("text/event-stream"))
                                || (contentType != null && contentType.contains("text/event-stream"))
                                || (uri != null && uri.contains("/stream"));
        }

        private boolean isClientDisconnect(Throwable throwable) {
                Throwable t = throwable;
                while (t != null) {
                        String msg = t.getMessage();
                        if (msg != null) {
                                String lower = msg.toLowerCase();
                                if (lower.contains("broken pipe") || lower.contains("connection reset by peer")
                                                || lower.contains("clientabortexception")) {
                                        return true;
                                }
                        }
                        t = t.getCause();
                }
                return false;
        }
}
