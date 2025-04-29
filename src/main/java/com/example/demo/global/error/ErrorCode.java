package com.example.demo.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // common 에러 0000 ~ 0999 --------------------------------
    COMMON_ERROR(HttpStatus.BAD_REQUEST, 0000, "오류가 발생했습니다"),
    COMMON_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 0001, "잘못된 입력값입니다"),
    COMMON_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 0002, "잘못된 타입입니다"),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 0003, "인증에 실패했습니다"),
    COMMON_ACCESS_DENIED(HttpStatus.FORBIDDEN, 0004, "접근 권한이 없습니다"),
    COMMON_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 0005, "엔티티를 찾을 수 없습니다"),
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 0006, "서버 오류가 발생했습니다"),

    // AUTH 관련 에러 1000 ~ 1999 --------------------------------
    AUTH_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1000, "인증 오류가 발생했습니다"),
    AUTH_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 1001, "잘못된 입력값입니다"),
    AUTH_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 1002, "잘못된 타입입니다"),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 1003, "인증에 실패했습니다"),
    AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, 1004, "접근 권한이 없습니다"),
    AUTH_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 1005, "엔티티를 찾을 수 없습니다"),
    AUTH_DUPLICATE_USER(HttpStatus.CONFLICT, 1006, "이미 가입된 사용자입니다"),
    AUTH_NOT_FOUND_USER(HttpStatus.NOT_FOUND, 1007, "존재하지 않는 사용자입니다"),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 1008, "비밀번호가 일치하지 않습니다"),


    // QR 관련 에러 2000 ~ 2999 --------------------------------
    QR_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2000, "QR 오류가 발생했습니다"),
    QR_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 2001, "잘못된 입력값입니다"),
    QR_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 2002, "잘못된 타입입니다"),
    QR_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 2003, "인증에 실패했습니다"),
    QR_ACCESS_DENIED(HttpStatus.FORBIDDEN, 2004, "접근 권한이 없습니다"),
    QR_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 2005, "엔티티를 찾을 수 없습니다"),

    // USER 관련 에러 3000 ~ 3999 --------------------------------
    USER_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3000, "사용자 오류가 발생했습니다"),
    USER_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 3001, "잘못된 입력값입니다"),
    USER_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 3002, "잘못된 타입입니다"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 3003, "인증에 실패했습니다"),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, 3004, "접근 권한이 없습니다"),
    USER_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, 3005, "엔티티를 찾을 수 없습니다"),

    // 100000 이상은 예외 처리 오류 코드 범위 --------------------------------
    OUT_OF_RANGE_ERROR(HttpStatus.BAD_REQUEST, 100000, "예외 처리 오류 코드 범위 초과");

    private final HttpStatus status;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus status, Integer code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}