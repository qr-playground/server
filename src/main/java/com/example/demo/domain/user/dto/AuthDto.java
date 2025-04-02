package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "AuthLoginRequestDto", description = "인증 요청 DTO")
    public static class Login {
        @NotBlank(message = "Login ID 작성해주세요.")
        private String loginId;

        @NotBlank(message = "Password 작성해주세요.")
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "AuthSignUpRequestDto", description = "회원가입 요청 DTO")
    public static class SignUp {
        @NotBlank(message = "Name 작성해주세요.")
        private String name;

        @NotBlank(message = "Login ID 작성해주세요.")
        @Size(min = 4, max = 20, message = "Login ID는 최소 4자 이상, 최대 20자 이하이어야 합니다.")
        private String loginId;

        @NotBlank(message = "Password 작성해주세요.")
        @Size(min = 4, max = 20, message = "Password는 최소 4자 이상, 최대 20자 이하이어야 합니다.")
        private String password;

        public User toEntity(PasswordEncoder passwordEncoder) {
            return User.builder()
                    .name(this.name)
                    .loginId(this.loginId)
                    .password(passwordEncoder.encode(this.password))
                    .role(Role.NORMAL)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "AuthResponseDto", description = "인증 응답 DTO")
    public static class Response {
        private UUID id;
        private String name;
        private String loginId;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean deleted;
        private String token;

        public static Response fromEntity(User user, String token) {
            return Response.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .loginId(user.getLoginId())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .deleted(user.getDeleted())
                    .token(token)
                    .build();
        }
    }
}