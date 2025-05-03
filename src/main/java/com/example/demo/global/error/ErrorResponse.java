package com.example.demo.global.error;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Boolean success;
    private final LocalDateTime timestamp;
    private final String code;
    private final String status;
    private final String message;
    private final String path;

    // 필드 검증 오류 정보 (400 Bad Request 시 사용)
    private final List<FieldError> fieldErrors; 

    // 검증 오류 응답 생성자
    private ErrorResponse(ErrorCode errorCode, String path, List<FieldError> fieldErrors) {
        this.success = false;
        this.timestamp = LocalDateTime.now();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().toString();
        this.message = errorCode.getMessage();
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    // 전체 오류 응답 생성자 (필드 오류 없음)
    private ErrorResponse(ErrorCode errorCode, String path) {
        this(errorCode, path, Collections.emptyList());
    }

    //기본 오류 생성 팩토리 (필드 오류 없음)
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode, path);
    }

    // 검증 오류 생성 팩토리
    public static ErrorResponse of(ErrorCode errorCode, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, path, fieldErrors);
    }

    // 검증 오류 정보를 담는 내부 static 클래스
    @Getter
    public static class FieldError {
        private final String field;
        private final String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}