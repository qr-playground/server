package com.example.demo.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    /**
     * 토큰 타입 (Bearer)
     */
    private String grantType;

    /**
     * 액세스 토큰
     */
    private String accessToken;

    /**
     * 리프레시 토큰
     */
    private String refreshToken;

    /**
     * 액세스 토큰 만료 시간 (밀리초)
     */
    private Long accessTokenExpiresIn;
}