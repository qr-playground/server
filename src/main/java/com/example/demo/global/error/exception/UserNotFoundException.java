package com.example.demo.global.error.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.global.error.ErrorCode;

public class UserNotFoundException extends UsernameNotFoundException {
    private final ErrorCode errorCode;

    // 생성자: 메시지와 ErrorCode를 함께 설정
    public UserNotFoundException(String phoneNumber) {
        super("해당 전화번호로 가입된 사용자를 찾을 수 없습니다: " + phoneNumber);
        this.errorCode = ErrorCode.AUTH_UNAUTHORIZED;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}