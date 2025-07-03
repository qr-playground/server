package com.example.demo.global.error;

import lombok.Getter;

@Getter
public class RateLimitErrorResponse extends ErrorResponse {

    private final long retryAfterSeconds;
    private final String retryAfterMessage;

    private RateLimitErrorResponse(ErrorCode errorCode, String path, long retryAfterSeconds) {
        super(errorCode, path);
        this.retryAfterSeconds = retryAfterSeconds;
        this.retryAfterMessage = generateRetryMessage(retryAfterSeconds);
    }

    public static RateLimitErrorResponse of(ErrorCode errorCode, String path, long retryAfterSeconds) {
        return new RateLimitErrorResponse(errorCode, path, retryAfterSeconds);
    }

    private String generateRetryMessage(long seconds) {
        if (seconds <= 0) {
            return "잠시 후 다시 시도해주세요.";
        } else if (seconds < 60) {
            return String.format("%d초 후에 다시 시도해주세요.", seconds);
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return String.format("%d분 후에 다시 시도해주세요.", minutes);
        } else {
            long hours = seconds / 3600;
            return String.format("%d시간 후에 다시 시도해주세요.", hours);
        }
    }
}