package com.example.demo.domain.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String phoneNumber;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }
}