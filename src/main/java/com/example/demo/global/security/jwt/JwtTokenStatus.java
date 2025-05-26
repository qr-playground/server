package com.example.demo.global.security.jwt;

public enum JwtTokenStatus {
    VALID,
    EXPIRED,
    INVALID_SIGNATURE,
    INVALID_TOKEN,
    UNSUPPORTED_TOKEN,
    MALFORMED_TOKEN,
    ILLEGAL_ARGUMENT,
    UNKNOWN_ERROR;
}
