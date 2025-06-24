package com.example.demo.domain.guestbook.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class GuestbookDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "GuestbookCreateRequest", description = "방명록 작성 요청 정보")
    public static class Create {
        private String deviceId; // 디바이스 ID
        private String name; // 방명록 작성자 이름
        private String phoneNumber; // 전화번호 (선택 사항)

        public Guestbook toEntity(QrcodeEvent qrcodeEvent) {
            return Guestbook.builder()
                    .qrcodeEvent(qrcodeEvent)
                    .deviceId(deviceId)
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "GuestbookResponse", description = "방명록 조회 응답 정보")
    public static class Response {
        private UUID id;
        private String name;
        private String phoneNumber;
        private LocalDateTime createdAt;
        private String shortId;
        private Integer maxAttendeeCount;
        private Integer availableAttendeeCount;
        private Boolean isAttendeeCountLimited;

        public static Response fromEntity(Guestbook guestbook) {
            return Response.builder()
                    .id(guestbook.getId())
                    .name(guestbook.getName())
                    .phoneNumber(guestbook.getPhoneNumber())
                    .createdAt(guestbook.getCreatedAt())
                    .shortId(guestbook.getQrcodeEvent().getShortId())
                    .maxAttendeeCount(guestbook.getQrcodeEvent().getQrcodeBenefit().getMaxAttendeeCount())
                    .availableAttendeeCount(guestbook.getQrcodeEvent().getQrcodeBenefit().getAvailableAttendeeCount())
                    .isAttendeeCountLimited(guestbook.getQrcodeEvent().getQrcodeBenefit().getIsAttendeeCountLimited())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(name = "GuestbookListResponse", description = "방명록 목록 조회 응답 정보")
    public static class ListResponse {
        private List<Response> guestbooks;
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

        public static ListResponse fromEntity(Page<Guestbook> guestbooks) {
            return ListResponse.builder()
                    .guestbooks(guestbooks.stream()
                            .map(Response::fromEntity)
                            .collect(Collectors.toList()))
                    .pagination(PaginationInfo.builder()
                            .totalItems(guestbooks.getTotalElements())
                            .totalPages(guestbooks.getTotalPages())
                            .currentPage(guestbooks.getNumber())
                            .pageSize(guestbooks.getSize())
                            .build())
                    .build();
        }
    }

    // @Data
    // @AllArgsConstructor
    // @NoArgsConstructor
    // @Builder
    // public static class ListResponse {
    // private List<Response> guestbooks;
    // private PaginationInfo pagination;

    // @Data
    // @AllArgsConstructor
    // @NoArgsConstructor
    // @Builder
    // public static class PaginationInfo {
    // private long totalItems;
    // private int totalPages;
    // private int currentPage;
    // private int pageSize;
    // private boolean hasNext;
    // private boolean hasPrevious;
    // }

    // public static ListResponse fromEntities(List<Guestbook> guestbooks,
    // PaginationInfo paginationInfo) {
    // List<Response> responseList = guestbooks.stream()
    // .map(Response::fromEntity)
    // .collect(Collectors.toList());

    // return ListResponse.builder()
    // .guestbooks(responseList)
    // .pagination(paginationInfo)
    // .build();
    // }
    // }
}