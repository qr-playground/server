package com.example.demo.global.interceptor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class WriteLsnResponseInterceptor implements HandlerInterceptor {

    private static final String HDR_LAST = "X-Last-LSN";

    private final JdbcTemplate masterJdbcTemplate;

    public WriteLsnResponseInterceptor(@Qualifier("masterJdbcTemplate") JdbcTemplate masterJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex) throws Exception {

        // 쓰기 메서드만 대상
        final String method = request.getMethod();
        final boolean isWriteMethod = "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)
                || "DELETE".equals(method);
        if (!isWriteMethod)
            return;

        // 2xx 성공 응답에만 헤더 부여 (원하면 201/204만 허용하도록 좁혀도 됨)
        final int status = response.getStatus();
        if (status < 200 || status >= 300)
            return;

        // 이미 설정된 경우 중복 방지
        if (response.getHeader(HDR_LAST) != null)
            return;

        // 마스터의 현재 LSN을 문자열로 조회
        try {
            String lsn = masterJdbcTemplate.queryForObject("select pg_current_wal_lsn()::text", String.class);
            if (lsn != null && !lsn.isBlank()) {
                response.setHeader(HDR_LAST, lsn);
            }
        } catch (Exception ignore) {
            // 필요시 로깅
        }
    }
}