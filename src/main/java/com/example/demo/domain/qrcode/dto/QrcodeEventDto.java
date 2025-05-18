package com.example.demo.domain.qrcode.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.demo.domain.image.entity.Image;
import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QrcodeEventDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "QrcodeEventCreateRequest", description = "QR 코드 이벤트 생성 요청 정보")
    public static class Create {

        // QrcodeEvent 정보
        private String title;
        private String description;
        private String secretCode;
        private LocalDateTime entryStartAt;
        private LocalDateTime entryEndAt;

        // QrcodeDesign 정보
        private String errorCorrectionLevel;
        private Boolean includeMargin;
        private String backgroundColor;
        private String pointColor;
        private Integer size;
        private String dotType;

        // 로고 이미지 관련 정보 nullable 처리
        private Integer logoVisualSize;
        private Double logoVisualRatio;
        private UUID logoImageId;

        public QrcodeEvent toEntity(User user) {
            return QrcodeEvent.builder()
                    .user(user)
                    .title(title)
                    .description(description)
                    .secretCode(secretCode)
                    .entryStartAt(entryStartAt)
                    .entryEndAt(entryEndAt)
                    .build();
        }

        // 로고 이미지 있는 경우
        public QrcodeDesign toEntity(QrcodeEvent qrcodeEvent, Image logoImage) {
            return QrcodeDesign.builder()
                    .qrcodeEvent(qrcodeEvent)
                    .errorCorrectionLevel(errorCorrectionLevel)
                    .includeMargin(includeMargin)
                    .backgroundColor(backgroundColor)
                    .pointColor(pointColor)
                    .size(size)
                    .dotType(dotType)
                    .logoImage(logoImage)
                    .logoVisualSize(logoVisualSize)
                    .logoVisualRatio(logoVisualRatio)
                    .build();
        }

        // 로고 이미지 없는 경우
        public QrcodeDesign toEntity(QrcodeEvent qrcodeEvent) {
            return QrcodeDesign.builder()
                    .qrcodeEvent(qrcodeEvent)
                    .errorCorrectionLevel(errorCorrectionLevel)
                    .includeMargin(includeMargin)
                    .backgroundColor(backgroundColor)
                    .pointColor(pointColor)
                    .size(size)
                    .dotType(dotType)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "QrcodeEventResponse", description = "QR 코드 이벤트 조회 응답 정보")
    public static class Response {

        private QrcodeInfo qrcodeInfo;

        public static Response fromEntity(QrcodeEvent qrcodeEvent, QrcodeDesign qrcodeDesign) {
            return Response.builder()
                    .qrcodeInfo(QrcodeInfo.fromEntity(qrcodeEvent, qrcodeDesign))
                    .build();
        }

        public static Response fromEntity(QrcodeEvent qrcodeEvent) {
            return Response.builder()
                    .qrcodeInfo(QrcodeInfo.fromEntity(qrcodeEvent))
                    .build();
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QrcodeInfo {
            private QrcodeEventInfo qrcodeEventInfo;
            private QrcodeDesignInfo qrcodeDesignInfo;

            public static QrcodeInfo fromEntity(QrcodeEvent qrcodeEvent, QrcodeDesign qrcodeDesign) {

                return QrcodeInfo.builder()
                        .qrcodeEventInfo(QrcodeEventInfo.fromEntity(qrcodeEvent))
                        .qrcodeDesignInfo(QrcodeDesignInfo.fromEntity(qrcodeDesign))
                        .build();
            }

            public static QrcodeInfo fromEntity(QrcodeEvent qrcodeEvent) {
                return QrcodeInfo.builder()
                        .qrcodeEventInfo(QrcodeEventInfo.fromEntity(qrcodeEvent))
                        .qrcodeDesignInfo(QrcodeDesignInfo.fromEntity(qrcodeEvent.getQrcodeDesign()))
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QrcodeEventInfo {
            private UUID id;
            private String shortId;
            private String title;
            private String description;
            private String secretCode;
            private LocalDateTime entryStartAt;
            private LocalDateTime entryEndAt;

            public static QrcodeEventInfo fromEntity(QrcodeEvent qrcodeEvent) {
                return QrcodeEventInfo.builder()
                        .id(qrcodeEvent.getId())
                        .shortId(qrcodeEvent.getShortId())
                        .title(qrcodeEvent.getTitle())
                        .description(qrcodeEvent.getDescription())
                        .secretCode(qrcodeEvent.getSecretCode())
                        .entryStartAt(qrcodeEvent.getEntryStartAt())
                        .entryEndAt(qrcodeEvent.getEntryEndAt())
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QrcodeDesignInfo {
            private UUID id;
            private String errorCorrectionLevel;
            private Boolean includeMargin;
            private String backgroundColor;
            private String pointColor;
            private Integer size;
            private String dotType;
            private Integer logoVisualSize;
            private Double logoVisualRatio;
            private UUID logoImageId;

            public static QrcodeDesignInfo fromEntity(QrcodeDesign qrcodeDesign) {
                UUID logoImageId = qrcodeDesign.getLogoImage() != null ? qrcodeDesign.getLogoImage().getId() : null;

                return QrcodeDesignInfo.builder()
                        .id(qrcodeDesign.getId())
                        .errorCorrectionLevel(qrcodeDesign.getErrorCorrectionLevel())
                        .includeMargin(qrcodeDesign.getIncludeMargin())
                        .backgroundColor(qrcodeDesign.getBackgroundColor())
                        .pointColor(qrcodeDesign.getPointColor())
                        .size(qrcodeDesign.getSize())
                        .dotType(qrcodeDesign.getDotType())
                        .logoVisualSize(qrcodeDesign.getLogoVisualSize())
                        .logoVisualRatio(qrcodeDesign.getLogoVisualRatio())
                        .logoImageId(logoImageId)
                        .build();
            }
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "QrcodeEventListResponse", description = "QR 코드 이벤트 목록 조회 응답 정보")
    public static class ListResponse {
        private List<Response.QrcodeInfo> qrcodeInfos;
        private PaginationInfo pagination;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PaginationInfo {
            private long totalItems;
            private int totalPages;
            private int currentPage;
            private int pageSize;
        }

        public static ListResponse fromEntity(List<QrcodeEvent> qrcodeEvents, PaginationInfo paginationInfo) {
            return ListResponse.builder()
                    .qrcodeInfos(qrcodeEvents.stream()
                            .map(Response.QrcodeInfo::fromEntity)
                            .collect(Collectors.toList()))
                    .pagination(paginationInfo)
                    .build();
        }
    }

}
