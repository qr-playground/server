package com.example.demo.domain.qrcode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.qrcode.dto.QrcodeEventDto;
import com.example.demo.domain.qrcode.service.QrcodeEventService;
import com.example.demo.global.security.user.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/qrcode/event")
@RequiredArgsConstructor
public class QrcodeEventController {

    private final QrcodeEventService qrcodeEventService;

    /**
     * QR 코드 이벤트 생성 API
     * 
     * @param QrcodeEventDto.Create QR 코드 이벤트 생성 요청 정보
     * @param userDetails           인증된 사용자 정보
     * @return 생성된 QR 코드 이벤트 정보
     */
    @PostMapping
    public ResponseEntity<QrcodeEventDto.Response> createQrcodeEvent(
            @Valid @RequestBody QrcodeEventDto.Create request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // QR 코드 생성 서비스 호출
        QrcodeEventDto.Response response = qrcodeEventService.createQrcodeEvent(request, userDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
