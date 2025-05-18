package com.example.demo.global.config;

import java.util.Arrays;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI(Swagger) 설정 클래스
 * 
 * 이 설정은 다음과 같은 역할을 합니다:
 * 1. API 문서의 기본 정보 설정 (제목, 설명, 버전, 연락처 등)
 * 2. 개발 및 운영 서버 정보 설정
 * 3. JWT 인증을 위한 보안 스키마 정의
 * 4. Swagger UI에서 API를 테스트할 때 필요한 보안 요구사항 설정
 * 5. API 엔드포인트를 그룹화하여 문서 가독성 향상
 * 6. JWT 토큰 파라미터 자동 추가를 위한 커스터마이저 설정
 * 
 * Swagger UI 접근 URL: http://서버주소:포트/swagger-ui
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI 기본 설정
     */
    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .title("QR World API 문서") // ! TODO: 타이틀 수정
                .description("Developer: suhwani.dev")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("suhwani.dev")
                        .email("suhwani.dev@gmail.com")
                        .url("https://github.com/su-hwani"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://springdoc.org"));

        // 개발 서버 설정
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("개발 서버");

        // 운영 서버 설정
        Server prodServer = new Server()
                .url("http://localhost:8080") // ! TODO: 실제 운영 서버 URL로 수정
                .description("운영 서버");

        // JWT 보안 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // OpenAPI 객체 생성 및 설정
        return new OpenAPI()
                .info(info)
                .servers(Arrays.asList(localServer, prodServer))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }

    /**
     * 일반 API 그룹 설정
     * 경로가 /api/로 시작하는 모든 엔드포인트를 포함
     */
    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 관리자 API 그룹 설정
     * 경로가 /admin/으로 시작하는 모든 엔드포인트를 포함
     */
    @Bean
    public GroupedOpenApi adminGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .build();
    }

    /**
     * JWT 토큰 파라미터 자동 추가 설정
     * 모든 API 요청에 Authorization 헤더 파라미터를 자동으로 추가
     */
    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(
                    new io.swagger.v3.oas.models.parameters.Parameter()
                            .in(ParameterIn.HEADER.toString())
                            .name("Authorization")
                            .description("JWT Token (Bearer 토큰)")
                            .required(true)
                            .schema(new io.swagger.v3.oas.models.media.StringSchema()));
            return operation;
        };
    }
}