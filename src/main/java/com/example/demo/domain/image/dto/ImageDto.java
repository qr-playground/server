package com.example.demo.domain.image.dto;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.image.entity.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ImageDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Create {
        private MultipartFile image;

        public Image toEntity(String url) {
            return Image.builder()
                    .url(url)
                    .name(image.getOriginalFilename())
                    .size(image.getSize())
                    .contentType(image.getContentType())
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private UUID id;
        private String url;
        private String name;
        private Long size;
        private String contentType;

        public static Response fromEntity(Image image) {
            return Response.builder()
                    .id(image.getId())
                    .url(image.getUrl())
                    .name(image.getName())
                    .size(image.getSize())
                    .contentType(image.getContentType())
                    .build();
        }
    }
}