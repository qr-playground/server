package com.example.demo.global.common;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice(basePackages = "com.example.demo")
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public SuccessResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        HttpStatus httpStatus = HttpStatus.resolve(status);

        // HTTP 상태를 확인할 수 없는 경우
        if (httpStatus == null) {
            return body;
        }

        // 성공 응답인 경우
        if (httpStatus.is2xxSuccessful()) {
            // String 타입 처리를 위한 특별 케이스
            if (body instanceof String) {
                try {
                    // String을 직렬화된 SuccessResponse로 변환
                    String json = objectMapper.writeValueAsString(new SuccessResponse<>(status, body));
                    // MediaType 설정
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return json;
                } catch (Exception e) {
                    // 오류 발생 시 원본 반환
                    return body;
                }
            }
            return new SuccessResponse<>(status, body);
        }

        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
}
