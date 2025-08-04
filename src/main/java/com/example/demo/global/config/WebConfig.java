package com.example.demo.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.global.interceptor.LoggingInterceptor;
import com.example.demo.global.interceptor.RateLimitInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.frontend.urls}")
    private List<String> frontendUrls;

    private final LoggingInterceptor loggingInterceptor;

    private final RateLimitInterceptor rateLimitInterceptor;

    // CORS 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("frontendUrls: {}", frontendUrls);
        registry.addMapping("/**")
                .allowedOrigins(frontendUrls.toArray(new String[0])) // 여러 프론트엔드 URL 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로깅 인터셉터
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");

        // Rate Limit 인터셉터
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**");
    }
}
