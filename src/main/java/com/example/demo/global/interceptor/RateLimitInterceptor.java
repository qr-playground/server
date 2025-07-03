package com.example.demo.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.global.error.exception.RateLimitException;
import com.example.demo.global.security.jwt.JwtTokenProvider;
import com.github.benmanes.caffeine.cache.Cache;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    // CacheConfig에 정의된 rateLimitCache Bean을 주입받습니다.
    private final Cache<String, Bucket> rateLimitCache;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return true;
        }

        String clientIp = request.getRemoteAddr();
        String deviceIdToken = request.getHeader("X-Device-ID");

        // JWT 토큰에서 UUID 추출
        String deviceUuid = extractDeviceUuid(deviceIdToken);

        log.info("clientIp: {}, deviceUuid: {}", clientIp, deviceUuid);
        RateLimitPlan plan = rateLimit.plan();

        // IP와 Device UUID를 조합하여 캐시 키 생성
        String cacheKey = generateCacheKey(plan, clientIp, deviceUuid);

        Bucket bucket = rateLimitCache.get(cacheKey, key -> plan.createBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 토큰 소비에 성공한 경우
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            log.debug("Rate Limit 통과 - 남은 토큰: {}, 키: {}", probe.getRemainingTokens(), cacheKey);
            return true; // 컨트롤러 로직 계속 진행
        } else {
            // 토큰 소비에 실패한 경우 (Too Many Requests)
            long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;

            // 429 Too Many Requests 에러를 발생시킵니다.
            throw new RateLimitException(plan.getErrorCode(), waitForRefillSeconds);
        }
    }

    private String extractDeviceUuid(String deviceIdToken) {
        if (deviceIdToken == null || deviceIdToken.trim().isEmpty()) {
            return null;
        }
        try {
            return jwtTokenProvider.getUuidFromDeviceIdToken(deviceIdToken);
        } catch (Exception e) {
            log.debug("Device ID 토큰에서 UUID 추출 실패: {}", e.getMessage());
            return null;
        }
    }

    private String generateCacheKey(RateLimitPlan plan, String clientIp, String deviceUuid) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(plan.name()).append(":");

        if (deviceUuid != null && !deviceUuid.trim().isEmpty()) {
            // Device UUID가 있는 경우: IP + Device UUID 조합
            keyBuilder.append(clientIp).append(":").append(deviceUuid);
            log.debug("Rate Limit 키 생성 - IP+DeviceUUID: {}", keyBuilder.toString());
        } else {
            // Device UUID가 없는 경우: IP만 사용
            keyBuilder.append(clientIp);
            log.debug("Rate Limit 키 생성 - IP Only: {}", keyBuilder.toString());
        }

        return keyBuilder.toString();
    }
}
