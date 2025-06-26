package com.example.demo.domain.guestbook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.service.GuestbookService;
import com.example.demo.global.interceptor.RateLimit;
import com.example.demo.global.interceptor.RateLimitPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping("/api/qrcode/{shortId}/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;

    /**
     * 방명록 생성
     * 
     * @param shortId  QR 코드 단축 ID
     * @param request  방명록 생성 요청 정보
     * @return 생성된 방명록 정보
     */
    @PostMapping("/")
    @RateLimit(plan = RateLimitPlan.GUESTBOOK_WRITE)
    public ResponseEntity<GuestbookDto.Response> createGuestbook(@PathVariable String shortId,
            @RequestBody GuestbookDto.Create request) {

        GuestbookDto.Response response = guestbookService.createGuestbook(shortId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 방명록 목록 조회
     * 
     * @param shortId  QR 코드 단축 ID
     * @param page    페이지 번호
     * @param size    페이지 크기
     * @return 방명록 목록 응답
     */
    @GetMapping("/")
    public ResponseEntity<GuestbookDto.ListResponse> getGuestbooks(@PathVariable String shortId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        GuestbookDto.ListResponse response = guestbookService.getGuestbooks(shortId, page, size);
        return ResponseEntity.ok(response);
    }
}
