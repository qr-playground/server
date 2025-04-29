package com.example.demo.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    /**
     * 사용자 전화번호 (식별자)
     */
    @NotBlank(message = "전화번호를 입력해주세요")
    private String phoneNumber;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}