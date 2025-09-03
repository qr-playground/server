package com.example.demo.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.global.interceptor.LoggingInterceptor;
import com.example.demo.global.interceptor.RateLimitInterceptor;
import com.example.demo.global.interceptor.ReplicaConsistencyInterceptor;
import com.example.demo.global.interceptor.WriteLsnResponseInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

        @Value("${app.frontend.url}")
        private String frontendUrl;

        @Value("${app.frontend.redirect-url}")
        private String frontendRedirectUrl;

        private final LoggingInterceptor loggingInterceptor;

        private final RateLimitInterceptor rateLimitInterceptor;

        private final ReplicaConsistencyInterceptor replicaConsistencyInterceptor;
        private final WriteLsnResponseInterceptor writeLsnResponseInterceptor;
        private final OpenEntityManagerInViewInterceptor osivInterceptor;

        // CORS 설정
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                log.info("frontendUrl: {}", frontendUrl);
                log.info("frontendRedirectUrl: {}", frontendRedirectUrl);
                registry.addMapping("/**")
                                .allowedOrigins(frontendUrl) // 여러 프론트엔드 URL 허용
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                                .allowedHeaders("*")
                                .allowCredentials(true);
        }

        @Override
        public void addInterceptors(@NonNull InterceptorRegistry registry) {
                // osiv 인터셉터
                registry.addWebRequestInterceptor(osivInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns(
                                                "/api/qrcode/**/guestbook/stream");

                // 로깅 인터셉터
                registry.addInterceptor(loggingInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");

                // Rate Limit 인터셉터
                registry.addInterceptor(rateLimitInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");

                // 레플리카 일관성 인터셉터 (읽기 요청에서 라우팅 신호 설정)
                registry.addInterceptor(replicaConsistencyInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");

                // 쓰기 요청 성공 시 LSN을 응답 헤더로 추가
                registry.addInterceptor(writeLsnResponseInterceptor)
                                .addPathPatterns("/api/**")
                                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**");
        }
}
