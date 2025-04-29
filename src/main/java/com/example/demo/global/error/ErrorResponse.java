package com.example.demo.global.error;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Boolean success;
    private final LocalDateTime timestamp;
    private final Integer code;
    private final String status;
    private final String message;
    private final String path;

    private ErrorResponse(ErrorCode errorCode, String path) {
        this.success = false;
        this.timestamp = LocalDateTime.now();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus().toString();
        this.message = errorCode.getMessage();
        this.path = path;
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode, path);
    }
}