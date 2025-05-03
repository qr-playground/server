package com.example.demo.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo.domain.qrcode.dto.QrcodeEventDto.Response.QrcodeInfo;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
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

        public static Response fromEntity(User user, List<QrcodeEvent> qrcodeEvents) {
            return Response.builder()
                    .id(user.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .qrcodeInfos(qrcodeEvents.stream().map(QrcodeInfo::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

}