package com.example.demo.global.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "에러 응답 DTO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    @Schema(description = "HTTP 상태 코드")
    private int code;

    @Schema(description = "에러 메시지")
    private String message;

    @Schema(description = "에러 발생 시간")
    private LocalDateTime timestamp;
    
    @Schema(description = "유효성 검증 에러 목록")
    private List<String> validationErrors;
}