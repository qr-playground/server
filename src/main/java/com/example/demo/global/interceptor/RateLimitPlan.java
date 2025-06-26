package com.example.demo.global.interceptor;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;

import com.example.demo.global.error.ErrorCode;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.Getter;

public enum RateLimitPlan {

    // 기본 플랜: 초당 10회 요청 가능
    DEFAULT(10, Duration.ofSeconds(1), ErrorCode.INTERCEPTOR_RATE_LIMIT_DEFAULT_EXCEEDED),
    
    // SMS 발송 플랜: 10분에 5회 요청 가능
    SMS_SEND(5, Duration.ofMinutes(10), ErrorCode.INTERCEPTOR_RATE_LIMIT_SMS_SEND_EXCEEDED),

    // 게스트북 작성 플랜: 분당 5회 요청 가능
    GUESTBOOK_WRITE(5, Duration.ofMinutes(1), ErrorCode.INTERCEPTOR_RATE_LIMIT_GUESTBOOK_WRITE_EXCEEDED);

    private final Bandwidth bandwidth;

    @Getter
    private final Duration duration;

    @Getter
    private final ErrorCode errorCode;

    RateLimitPlan(long capacity, Duration duration, ErrorCode errorCode) {
        this.bandwidth = Bandwidth.classic(capacity, Refill.intervally(capacity, duration));
        this.duration = duration;
        this.errorCode = errorCode;
    }

    public Bucket createBucket() {
        return Bucket.builder()
                .addLimit(this.bandwidth)
                .build();
    }

    /**
     * @return 모든 Plan 중 가장 긴 duration에 1분의 여유시간을 더한 값
     */
    public static Duration getMaximumDuration() {
        return Arrays.stream(values())
                .map(RateLimitPlan::getDuration)
                .max(Comparator.naturalOrder())
                .orElse(Duration.ofMinutes(1))
                .plusMinutes(1);
    }
}