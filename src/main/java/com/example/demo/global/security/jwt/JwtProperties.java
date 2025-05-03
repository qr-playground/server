package com.example.demo.global.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 토큰 타입 (Bearer)
     */
    private String grantType = "Bearer";

    /**
     * JWT 서명에 사용할 비밀 키 (Base64 인코딩된 문자열)
     * 최소 256비트(32바이트) 이상의 키를 사용해야 합니다.
     */
    private String secret;

    /**
     * 액세스 토큰 유효 시간 (초 단위)
     * 기본값: 30분 (1800초)
     */
    private long tokenValidityInSeconds = 1800;

    /**
     * 리프레시 토큰 유효 시간 (초 단위)
     * 기본값: 7일 (604800초)
     */
    private long refreshTokenValidityInSeconds = 604800;

    /**
     * 인증 헤더 이름
     */
    private String authorizationHeader = "Authorization";

    /**
     * Bearer 토큰 접두사
     */
    private String bearerPrefix = "Bearer ";
}