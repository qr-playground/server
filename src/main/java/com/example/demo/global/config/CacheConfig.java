package com.example.demo.global.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, String> verificationCodeCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 10분 TTL
                .maximumSize(10_000) // 최대 10,000개
                .build();
    }
    
    @Bean
    public Cache<String, Boolean> verifiedPhoneNumberCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES) // 60분 TTL
                .maximumSize(10_000) // 최대 10,000개
                .build();
    }
}