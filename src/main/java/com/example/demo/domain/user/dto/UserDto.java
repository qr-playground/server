package com.example.demo.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo.domain.qrcode.dto.QrcodeEventDto.Response.QrcodeInfo;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "UserResponse", description = "사용자 조회 응답 정보")
    public static class Response {
        private UUID id;
        private String phoneNumber;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // qr 코드 정보
        private List<QrcodeInfo> qrcodeInfos;

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