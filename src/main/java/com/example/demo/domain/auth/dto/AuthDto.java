package com.example.demo.domain.auth.dto;

import java.util.UUID;

import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @NotBlank(message = "전화번호를 입력해주세요")
        private String phoneNumber;

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Signup {
        @NotBlank(message = "전화번호를 입력해주세요")
        private String phoneNumber;

        @NotBlank(message = "비밀번호를 입력해주세요")
        private String password;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .phoneNumber(this.phoneNumber)
                    .password(encodedPassword)
                    .role(Role.USER)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private UserInfo userInfo;
        private TokenInfo tokenInfo;

        public static Response of(User user) {
            return Response.builder()
                    .userInfo(UserInfo.builder()
                            .id(user.getId())
                            .phoneNumber(user.getPhoneNumber())
                            .role(user.getRole())
                            .build())
                    .build();
        }

        public static Response of(User user, TokenDto tokenDto) {
            return Response.builder()
                    .userInfo(UserInfo.builder()
                            .id(user.getId())
                            .phoneNumber(user.getPhoneNumber())
                            .role(user.getRole())
                            .build())
                    .tokenInfo(TokenInfo.builder()
                            .grantType(tokenDto.getGrantType())
                            .accessToken(tokenDto.getAccessToken())
                            .refreshToken(tokenDto.getRefreshToken())
                            .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                            .build())
                    .build();
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UserInfo {
            private UUID id;
            private String phoneNumber;
            private Role role;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TokenInfo {
            private String grantType;
            private String accessToken;
            private String refreshToken;
            private Long accessTokenExpiresIn;
        }
    }
}