package com.example.demo.domain.qrcode.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QrcodeEventDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        // QR 코드 이벤트 정보
        @NotBlank(message = "제목을 입력해주세요")
        private String title;

        @NotNull(message = "설명을 입력해주세요")
        private String description;

        private String groupCode;

        @NotNull(message = "입장 시작 시간을 입력해주세요")
        private LocalDateTime entryStartAt;

        @NotNull(message = "입장 종료 시간을 입력해주세요")
        private LocalDateTime entryEndAt;

        // QR 코드 디자인 정보
        @NotBlank(message = "상단 텍스트를 입력해주세요")
        private String topText;

        @NotBlank(message = "하단 텍스트를 입력해주세요")
        private String bottomText;

        @NotBlank(message = "중앙 텍스트를 입력해주세요")
        private String centerText;

        @NotNull(message = "상단 폰트 크기를 입력해주세요")
        private Integer topFontSize;

        @NotNull(message = "하단 폰트 크기를 입력해주세요")
        private Integer bottomFontSize;

        @NotNull(message = "테두리 두께를 입력해주세요")
        private Integer borderThickness;

        public QrcodeEvent toEntity(User user) {
            return QrcodeEvent.builder()
                    .user(user)
                    .title(title)
                    .description(description)
                    .groupCode(groupCode)
                    .entryStartAt(entryStartAt)
                    .entryEndAt(entryEndAt)
                    .build();
        }

        public QrcodeDesign toEntity(QrcodeEvent qrcodeEvent) {
            return QrcodeDesign.builder()
                    .qrcodeEvent(qrcodeEvent)
                    .topText(topText)
                    .bottomText(bottomText)
                    .centerText(centerText)
                    .topFontSize(topFontSize)
                    .bottomFontSize(bottomFontSize)
                    .borderThickness(borderThickness)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private QrcodeInfo qrcodeInfo;

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

            public static QrcodeInfo fromEntity(QrcodeEvent qrcodeEvent) {
                QrcodeDesign qrcodeDesign = qrcodeEvent.getQrcodeDesign();
                return QrcodeInfo.builder()
                        .qrcodeEventInfo(QrcodeEventInfo.fromEntity(qrcodeEvent))
                        .qrcodeDesignInfo(qrcodeDesign == null ? null : QrcodeDesignInfo.fromEntity(qrcodeDesign))
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class QrcodeEventInfo {
            private UUID id;
            private String title;
            private String description;
            private String groupCode;
            private LocalDateTime entryStartAt;
            private LocalDateTime entryEndAt;

            public static QrcodeEventInfo fromEntity(QrcodeEvent qrcodeEvent) {
                return QrcodeEventInfo.builder()
                        .id(qrcodeEvent.getId())
                        .title(qrcodeEvent.getTitle())
                        .description(qrcodeEvent.getDescription())
                        .groupCode(qrcodeEvent.getGroupCode())
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
            private String topText;
            private String bottomText;
            private String centerText;
            private Integer topFontSize;
            private Integer bottomFontSize;
            private Integer borderThickness;

            public static QrcodeDesignInfo fromEntity(QrcodeDesign qrcodeDesign) {
                return QrcodeDesignInfo.builder()
                        .id(qrcodeDesign.getId())
                        .topText(qrcodeDesign.getTopText())
                        .bottomText(qrcodeDesign.getBottomText())
                        .centerText(qrcodeDesign.getCenterText())
                        .topFontSize(qrcodeDesign.getTopFontSize())
                        .bottomFontSize(qrcodeDesign.getBottomFontSize())
                        .borderThickness(qrcodeDesign.getBorderThickness())
                        .build();
            }
        }
    }

}
