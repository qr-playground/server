package com.example.demo.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "UserCreateRequestDto", description = "사용자 생성 요청 DTO")
    public static class Create {
        @NotBlank(message = "Name 작성해주세요.")
        private String name;

        @NotBlank(message = "Login ID 작성해주세요.")
        private String loginId;

        @NotBlank(message = "Password 작성해주세요.")
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
    @Schema(name = "UserResponseDto", description = "사용자 응답 DTO")
    public static class Response {
        private UUID id;
        private String name;
        private String loginId;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean deleted;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .loginId(user.getLoginId())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .deleted(user.getDeleted())
                    .build();
        }

        public static List<Response> fromEntityList(List<User> userList) {
            return userList.stream()
                    .map(Response::fromEntity)
                    .collect(Collectors.toList());
        }
    }
}