package com.example.demo.global.exception;

import org.springframework.http.HttpStatus;

public class AlreadyReturnedException extends BaseException {
    public AlreadyReturnedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}