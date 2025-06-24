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
    public static class Refresh {
        private String refreshToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendVerificationCode {
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyVerificationCode {
        private String phoneNumber;
        private String verificationCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private UserInfo userInfo;
        private TokenInfo tokenInfo;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .userInfo(UserInfo.fromEntity(user))
                    .build();
        }

        public static Response fromEntity(User user, TokenDto tokenDto) {
            return Response.builder()
                    .userInfo(UserInfo.fromEntity(user))
                    .tokenInfo(TokenInfo.fromDto(tokenDto))
                    .build();
        }

        public static Response fromEntity(TokenDto tokenDto) {
            return Response.builder()
                    .tokenInfo(TokenInfo.fromDto(tokenDto))
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

            public static UserInfo fromEntity(User user) {
                return UserInfo.builder()
                        .id(user.getId())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .build();
            }
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

            public static TokenInfo fromDto(TokenDto tokenDto) {
                return TokenInfo.builder()
                        .grantType(tokenDto.getGrantType())
                        .accessToken(tokenDto.getAccessToken())
                        .refreshToken(tokenDto.getRefreshToken())
                        .accessTokenExpiresIn(tokenDto.getAccessTokenExpiresIn())
                        .build();
            }
        }
    }
}