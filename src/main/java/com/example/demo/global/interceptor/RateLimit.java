package com.example.demo.global.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 적용할 Rate Limit 정책(Plan)을 지정합니다.
     * 기본값은 DEFAULT 플랜입니다.
     */
    RateLimitPlan plan() default RateLimitPlan.DEFAULT;
}