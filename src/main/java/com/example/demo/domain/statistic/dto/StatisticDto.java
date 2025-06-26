package com.example.demo.domain.statistic.dto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.example.demo.domain.statistic.entity.Statistic;
import com.example.demo.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StatisticDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "StatisticResponse", description = "통계 조회 응답 정보")
    public static class Response {
        private List<UserQrcodeEventCount> userQrcodeEventCounts;
        private PaginationInfo pagination;
        private StatisticInfo statisticInfo;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(name = "StatisticInfo", description = "전체 통계 정보")
        public static class StatisticInfo {
            private long totalUserCount;
            private long totalQrcodeEventCount;
            private long totalGuestbookCount;
            private double avgQrcodeEventsPerUser;
            private double avgGuestbooksPerQrcodeEvent;

            public static StatisticInfo fromEntity(
                    Statistic statistic) {
                return StatisticInfo.builder()
                        .totalUserCount(statistic.getTotalUserCount())
                        .totalQrcodeEventCount(statistic.getTotalQrcodeEventCount())
                        .totalGuestbookCount(statistic.getTotalGuestbookCount())
                        .avgQrcodeEventsPerUser(
                                roundToOneDecimalPlace(statistic.getAvgQrcodeEventsPerUser()))
                        .avgGuestbooksPerQrcodeEvent(
                                roundToOneDecimalPlace(statistic.getAvgGuestbooksPerQrcodeEvent()))
                        .build();
            }

            private static double roundToOneDecimalPlace(double value) {
                return Math.round(value * 100) / 100.0;
            }
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Schema(name = "UserQrcodeEventCount", description = "사용자별 QR코드 이벤트 수")
        public static class UserQrcodeEventCount {
            private UUID userId;
            private String phoneNumber;
            private long qrcodeEventCount;

            public static UserQrcodeEventCount fromEntity(User user) {
                return UserQrcodeEventCount.builder()
                        .userId(user.getId())
                        .phoneNumber(user.getPhoneNumber())
                        .qrcodeEventCount(user.getQrcodeEvents().size())
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PaginationInfo {
            private long totalUserCount;
            private int totalPages;
            private int currentPage;
            private int pageSize;

            public static PaginationInfo fromEntity(Page<User> users) {
                return PaginationInfo.builder()
                        .totalUserCount(users.getTotalElements())
                        .totalPages(users.getTotalPages())
                        .currentPage(users.getNumber())
                        .pageSize(users.getSize())
                        .build();
            }
        }

        public static Response fromEntity(Page<User> users) {
            return Response.builder()
                    .userQrcodeEventCounts(users.stream()
                            .map(UserQrcodeEventCount::fromEntity)
                            .collect(Collectors.toList()))
                    .pagination(PaginationInfo.fromEntity(users))
                    .build();
        }

        public static Response fromEntity(Statistic statistic) {
            return Response.builder()
                    .statisticInfo(StatisticInfo.fromEntity(statistic))
                    .build();
        }
    }
}