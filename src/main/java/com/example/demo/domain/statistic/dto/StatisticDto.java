package com.example.demo.domain.statistic.dto;

import java.util.List;
import java.util.UUID;

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
    @Schema(name = "UserQrcodeEventCount", description = "사용자별 QR코드 이벤트 수")
    public static class UserQrcodeEventCount {
        private UUID userId;
        private String phoneNumber;
        private long qrcodeEventCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "StatisticResponse", description = "통계 조회 응답 정보")
    public static class Response {
        private List<UserQrcodeEventCount> userQrcodeEventCounts;
        private long totalUserCount;
        private int totalPages;
        private int currentPage;
        private int pageSize;

        public static Response fromEntity(List<UserQrcodeEventCount> userQrcodeEventCounts, long totalUserCount,
                int totalPages, int currentPage, int pageSize) {
            return Response.builder()
                    .userQrcodeEventCounts(userQrcodeEventCounts)
                    .totalUserCount(totalUserCount)
                    .totalPages(totalPages)
                    .currentPage(currentPage)
                    .pageSize(pageSize)
                    .build();
        }
    }
}