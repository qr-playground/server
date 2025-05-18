package com.example.demo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.global.security.jwt.JwtAccessDeniedHandler;
import com.example.demo.global.security.jwt.JwtAuthenticationEntryPoint;
import com.example.demo.global.security.jwt.JwtAuthenticationFilter;
import com.example.demo.global.security.jwt.JwtProperties;
import com.example.demo.global.security.jwt.JwtTokenProvider;
import com.example.demo.global.security.user.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtTokenProvider jwtTokenProvider;
        private final CustomUserDetailsService customUserDetailsService;
        private final JwtProperties jwtProperties;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

        // 비밀번호 암호화 인코더
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // 인증 관리자 설정
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        // JWT 인증 필터
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
                return new JwtAuthenticationFilter(jwtTokenProvider, jwtProperties);
        }

        // 보안 필터 체인 설정
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // CSRF 보호 비활성화 (REST API이므로)
                                .csrf(AbstractHttpConfigurer::disable)

                                // 세션 사용 안함 (JWT 사용)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // 인증/권한 예외 처리 구성
                                .exceptionHandling(handling -> handling
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))

                                // 요청에 대한 인가 규칙 설정
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/users").authenticated()
                                                .requestMatchers("/api/qrcode/event").authenticated()
                                                .requestMatchers("/api/qrcode/{shortId}/").permitAll()
                                                // 인증 없이 접근 가능한 경로
                                                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**")
                                                .permitAll()
                                                // 관리자 권한 필요
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/api/image/**").authenticated()
                                                // 그 외 모든 요청은 인증 필요
                                                .anyRequest().permitAll())

                                // 폼 로그인 비활성화 (JWT 사용)
                                .formLogin(AbstractHttpConfigurer::disable)

                                // HTTP Basic 인증 비활성화
                                .httpBasic(AbstractHttpConfigurer::disable)

                                // JWT 필터 추가
                                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

                // UserDetailsService 설정
                http.userDetailsService(customUserDetailsService);

                return http.build();
        }
}