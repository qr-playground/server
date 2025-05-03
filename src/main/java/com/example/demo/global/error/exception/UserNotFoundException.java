package com.example.demo.global.error.exception;

import com.example.demo.global.error.ErrorCode;

public class UserNotFoundException extends CustomException {

    // 생성자: 메시지와 ErrorCode를 함께 설정
    public UserNotFoundException() {
        super(ErrorCode.AUTH_UNAUTHORIZED);
    }
}