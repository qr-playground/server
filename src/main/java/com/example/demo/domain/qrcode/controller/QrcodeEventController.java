package com.example.demo.domain.qrcode.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * QR 코드 이벤트 조회 API
     * 
     * @param shortId QR 코드 이벤트 조회 요청 정보
     * @return 조회된 QR 코드 이벤트 정보
     */
    @GetMapping("/{shortId}")
    public ResponseEntity<QrcodeEventDto.Response> getQrcodeEvent(@PathVariable String shortId) {
        QrcodeEventDto.Response response = qrcodeEventService.getQrcodeEventByShortId(shortId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자의 QR 코드 이벤트 조회 API
     * 
     * @param userDetails 인증된 사용자 정보
     * @return 사용자의 QR 코드 이벤트 정보
     */
    @GetMapping("/user")
    public ResponseEntity<QrcodeEventDto.ListResponse> getUserQrcodeEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        QrcodeEventDto.ListResponse response = qrcodeEventService.getUserQrcodeEvents(userDetails.getUser(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * QR 코드 이벤트 종료 API
     * 
     * @param shortId     QR 코드 이벤트 종료 요청 정보
     * @param userDetails 인증된 사용자 정보
     * @return 종료된 QR 코드 이벤트 정보
     */
    @PostMapping("/{shortId}/terminate")
    public ResponseEntity<QrcodeEventDto.Response> terminateQrcodeEvent(@PathVariable String shortId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        QrcodeEventDto.Response response = qrcodeEventService.terminateQrcodeEvent(shortId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * QR 코드 이벤트 삭제 API
     * 
     * @param shortId     QR 코드 이벤트 삭제 요청 정보
     * @param userDetails 인증된 사용자 정보
     * @return 삭제된 QR 코드 이벤트 정보
     */
    @PostMapping("/{shortId}/delete")
    public ResponseEntity<QrcodeEventDto.Response> deleteQrcodeEvent(@PathVariable String shortId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        QrcodeEventDto.Response response = qrcodeEventService.deleteQrcodeEvent(shortId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }

}
