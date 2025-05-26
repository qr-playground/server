package com.example.demo.global.security.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.demo.global.security.user.CustomUserDetails;
import com.example.demo.global.security.user.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
    private final CustomUserDetailsService customUserDetailsService;
    private final Key key;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;

    public JwtTokenProvider(JwtProperties jwtProperties, CustomUserDetailsService customUserDetailsService) {
        // JwtProperties에서 설정값 가져오기
        String secret = jwtProperties.getSecret();
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = jwtProperties.getTokenValidityInSeconds() * 1000;
        this.refreshTokenValidityInMilliseconds = jwtProperties.getRefreshTokenValidityInSeconds() * 1000;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * 사용자 인증 정보를 기반으로 JWT 액세스 토큰 생성
     */
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    /**
     * 리프레시 토큰 생성
     */
    public String createRefreshToken(String username) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        String phoneNumber = claims.getSubject();
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(phoneNumber);

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * 토큰에서 사용자 이름(ID) 추출
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * 토큰 유효성 검증
     */
    public JwtTokenStatus validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return JwtTokenStatus.VALID;
        } catch (SecurityException e) {
            return JwtTokenStatus.INVALID_SIGNATURE;
        } catch (MalformedJwtException e) {
            return JwtTokenStatus.MALFORMED_TOKEN;
        } catch (ExpiredJwtException e) {
            return JwtTokenStatus.EXPIRED;
        } catch (UnsupportedJwtException e) {
            return JwtTokenStatus.UNSUPPORTED_TOKEN;
        } catch (IllegalArgumentException e) {
            return JwtTokenStatus.ILLEGAL_ARGUMENT;
        } catch (Exception e) {
            return JwtTokenStatus.UNKNOWN_ERROR;
        }
    }

    /**
     * 토큰의 만료 시간 조회
     */
    public Long getAccessTokenExpiresIn() {
        return tokenValidityInMilliseconds;
    }
}