package com.example.demo.global.error.exception;

import com.example.demo.global.error.ErrorCode;

import lombok.Getter;

@Getter
public class RateLimitException extends CustomException {
    private final long retryAfterSeconds;
    private final long remainingTokens;

    public RateLimitException(ErrorCode errorCode, long retryAfterSeconds, long remainingTokens) {
        super(errorCode);
        this.retryAfterSeconds = retryAfterSeconds;
        this.remainingTokens = remainingTokens;
    }

    public RateLimitException(ErrorCode errorCode, long retryAfterSeconds) {
        this(errorCode, retryAfterSeconds, 0);
    }
}