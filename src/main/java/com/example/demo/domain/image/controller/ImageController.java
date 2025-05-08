package com.example.demo.domain.image.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.image.dto.ImageDto;
import com.example.demo.domain.image.service.ImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 생성 API
     * 
     * @param ImageDto.Create 이미지 생성 요청 정보
     * @return 생성된 이미지 정보
     */
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ImageDto.Response> createImage(
            @Valid @ModelAttribute ImageDto.Create request) {

        log.info("이미지 생성 요청: {}", request);
        log.info("request.image: {}", request.getImage());

        // 이미지 생성 서비스 호출
        ImageDto.Response response = imageService.createImage(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDto.Response> getImage(@PathVariable UUID id) {
        ImageDto.Response response = imageService.findById(id);
        return ResponseEntity.ok(response);
    }
}
