package com.example.demo.global.common;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class SuccessResponse<T> {

    private final boolean success = true;
    private final int status;
    private final T data;
    private final LocalDateTime timestamp;

    public SuccessResponse(int status, T data) {
        this.status = status;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> SuccessResponse<T> of(T data, int status) {
        return new SuccessResponse<>(status, data);
    }
}
