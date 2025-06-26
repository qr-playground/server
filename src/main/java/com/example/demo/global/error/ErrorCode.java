package com.example.demo.global.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // common 에러 0000 ~ 0999 --------------------------------
    COMMON_ERROR(HttpStatus.BAD_REQUEST, "E0000", "오류가 발생했습니다"),
    COMMON_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E0001", "잘못된 입력값입니다"),
    COMMON_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E0002", "잘못된 타입입니다"),
    COMMON_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E0003", "인증에 실패했습니다"),
    COMMON_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E0004", "접근 권한이 없습니다"),
    COMMON_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E0005", "엔티티를 찾을 수 없습니다"),
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0006", "서버 오류가 발생했습니다"),
    COMMON_QUERY_FAILED(HttpStatus.BAD_REQUEST, "E0007", "쿼리 실행 실패"),
    COMMON_DB_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "E0009", "DB 연결 실패"),

    // AUTH 관련 에러 1000 ~ 1999 --------------------------------
    AUTH_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E1000", "인증 오류가 발생했습니다"),
    AUTH_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E1001", "잘못된 입력값입니다"),
    AUTH_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E1002", "잘못된 타입입니다"),
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E1003", "인증에 실패했습니다"),
    AUTH_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E1004", "접근 권한이 없습니다"),
    AUTH_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E1005", "엔티티를 찾을 수 없습니다"),
    AUTH_DUPLICATE_USER(HttpStatus.CONFLICT, "E1006", "이미 가입된 사용자입니다"),
    AUTH_NOT_FOUND_USER(HttpStatus.NOT_FOUND, "E1007", "존재하지 않는 사용자입니다"),
    AUTH_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "E1008", "비밀번호가 일치하지 않습니다"),
    AUTH_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "E1009", "리프레시 토큰이 유효하지 않습니다"),
    AUTH_NOT_VERIFIED_PHONE_NUMBER(HttpStatus.CONFLICT, "E1010", "인증되지 않았거나, 인증 시간이 지났습니다."),
    AUTH_SEND_VERIFICATION_CODE_FAILED(HttpStatus.BAD_REQUEST, "E1011", "인증 코드 발송 실패"),
    AUTH_VERIFY_VERIFICATION_CODE_FAILED(HttpStatus.BAD_REQUEST, "E1012", "인증 코드 검증 실패"),

    // QR 관련 에러 2000 ~ 2999 --------------------------------
    QRCODE_EVENT_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E2000", "QR 오류가 발생했습니다"),
    QRCODE_EVENT_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E2001", "잘못된 입력값입니다"),
    QRCODE_EVENT_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E2002", "잘못된 타입입니다"),
    QRCODE_EVENT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E2003", "인증에 실패했습니다"),
    QRCODE_EVENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E2004", "접근 권한이 없습니다"),
    QRCODE_EVENT_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E2005", "엔티티를 찾을 수 없습니다"),
    QRCODE_EVENT_LOGO_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E2006", "로고 이미지를 찾을 수 없습니다"),
    QRCODE_EVENT_ENTRY_NOT_OPEN(HttpStatus.CONFLICT, "E2007", "QR 코드 이벤트 참여 시간이 아닙니다"),

    // USER 관련 에러 3000 ~ 3999 --------------------------------
    USER_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E3000", "사용자 오류가 발생했습니다"),
    USER_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E3001", "잘못된 입력값입니다"),
    USER_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E3002", "잘못된 타입입니다"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E3003", "인증에 실패했습니다"),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E3004", "접근 권한이 없습니다"),
    USER_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E3005", "엔티티를 찾을 수 없습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E3006", "사용자를 찾을 수 없습니다"),

    // IMAGE 관련 에러 4000 ~ 4999 --------------------------------
    IMAGE_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E4000", "이미지 오류가 발생했습니다"),
    IMAGE_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E4001", "잘못된 입력값입니다"),
    IMAGE_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E4002", "잘못된 타입입니다"),
    IMAGE_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E4003", "인증에 실패했습니다"),
    IMAGE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E4004", "접근 권한이 없습니다"),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "E4005", "엔티티를 찾을 수 없습니다"),
    IMAGE_S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E4006", "S3 업로드 실패"),

    // GUESTBOOK 관련 에러 5000 ~ 5999 --------------------------------
    GUESTBOOK_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E5000", "방명록 오류가 발생했습니다"),
    GUESTBOOK_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E5001", "잘못된 입력값입니다"),
    GUESTBOOK_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E5002", "잘못된 타입입니다"),
    GUESTBOOK_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E5003", "인증에 실패했습니다"),
    GUESTBOOK_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E5004", "접근 권한이 없습니다"),
    GUESTBOOK_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E5005", "엔티티를 찾을 수 없습니다"),
    GUESTBOOK_QRCODE_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E5006", "QR 코드 이벤트를 찾을 수 없습니다"),
    GUESTBOOK_QRCODE_EVENT_ENTRY_ENDED(HttpStatus.BAD_REQUEST, "E5007", "QR 코드 이벤트 참여가 종료되었습니다"),

    // QRCODE_BENEFIT 관련 에러 6000 ~ 6999 --------------------------------
    QRCODE_BENEFIT_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E6000", "QR 코드 이벤트 이점 오류가 발생했습니다"),
    QRCODE_BENEFIT_INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E6001", "잘못된 입력값입니다"),
    QRCODE_BENEFIT_INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "E6002", "잘못된 타입입니다"),
    QRCODE_BENEFIT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E6003", "인증에 실패했습니다"),
    QRCODE_BENEFIT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "E6004", "접근 권한이 없습니다"),
    QRCODE_BENEFIT_ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "E6005", "엔티티를 찾을 수 없습니다"),
    QRCODE_BENEFIT_ENTRY_ENDED(HttpStatus.CONFLICT, "E6006", "QR 코드 이벤트 참여가 종료되었습니다"),

    // STATISTIC 관련 에러 7000 ~ 7999 --------------------------------
    STATISTIC_NOT_FOUND(HttpStatus.NOT_FOUND, "E7000", "통계 정보를 찾을 수 없습니다"),

    // INTERCEPTOR 관련 에러 99000 ~ 99999 --------------------------------
    INTERCEPTOR_UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E99000", "인터셉터 오류가 발생했습니다"),
    INTERCEPTOR_RATE_LIMIT_DEFAULT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E99001", "기본 요청 횟수를 초과했습니다"),
    INTERCEPTOR_RATE_LIMIT_SMS_SEND_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E99002", "SMS 발송 요청 횟수를 초과했습니다"),
    INTERCEPTOR_RATE_LIMIT_GUESTBOOK_WRITE_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E99003", "게스트북 작성 요청 횟수를 초과했습니다"),
    

    // 예외 처리 범위 초과 --------------------------------
    OUT_OF_RANGE_ERROR(HttpStatus.BAD_REQUEST, "E100000", "예외 처리 오류 코드 범위 초과");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}