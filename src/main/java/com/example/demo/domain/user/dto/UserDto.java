package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class UserDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Create {
        @NotBlank(message = "핸드폰 번호를 작성해주세요.")
        private String phoneNumber;

        @NotBlank(message = "비밀번호를 작성해주세요.")
        private String password;

        public User toEntity() {
            return User.builder()
                    .phoneNumber(this.phoneNumber)
                    .password(this.password)
                    .role(Role.USER)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String phoneNumber;
        private Role role;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .id(user.getId())
                    .phoneNumber(user.getPhoneNumber())
                    .role(user.getRole())
                    .build();
        }
    }

}