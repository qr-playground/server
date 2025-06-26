package com.example.demo.global.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;
import com.github.benmanes.caffeine.cache.Cache;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    // CacheConfig에 정의된 rateLimitCache Bean을 주입받습니다.
    private final Cache<String, Bucket> rateLimitCache;

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
        RateLimitPlan plan = rateLimit.plan();

        String cacheKey = plan.name() + ":" + clientIp;

        Bucket bucket = rateLimitCache.get(cacheKey, key -> plan.createBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // 토큰 소비에 성공한 경우
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true; // 컨트롤러 로직 계속 진행
        } else {
            // 토큰 소비에 실패한 경우 (Too Many Requests)
            long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefillSeconds));

            // 429 Too Many Requests 에러를 발생시킵니다.
            throw new CustomException(plan.getErrorCode());
        }
    }
}
