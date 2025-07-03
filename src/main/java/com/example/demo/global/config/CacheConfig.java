package com.example.demo.global.config;

import com.example.demo.global.interceptor.RateLimitPlan;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;

import io.github.bucket4j.Bucket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, String> verificationCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 TTL
                .maximumSize(10_000) // 최대 10,000개
                .scheduler(Scheduler.systemScheduler())
                .build();
    }
    
    @Bean
    public Cache<String, Boolean> verifiedPhoneNumberCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES) // 60분 TTL
                .maximumSize(10_000) // 최대 10,000개
                .scheduler(Scheduler.systemScheduler())
                .build();
    }

    @Bean
    public Cache<String, Bucket> rateLimitCache() {
        Duration cacheMaxExpireDuration = RateLimitPlan.getMaximumDuration();

        return Caffeine.newBuilder()
                .expireAfterAccess(cacheMaxExpireDuration)
                .maximumSize(20_000) // 최대 20,000개의 IP/Plan 조합을 저장
                .scheduler(Scheduler.systemScheduler())
                .build();
    }
}