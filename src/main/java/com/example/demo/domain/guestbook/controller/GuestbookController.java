package com.example.demo.domain.guestbook.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.service.GuestbookService;
import com.example.demo.global.service.SseService;
import com.example.demo.global.util.CursorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/qrcode/{shortId}/guestbook")
@RequiredArgsConstructor
public class GuestbookController {

    private final GuestbookService guestbookService;
    private final SseService sseService;

    /**
     * 방명록 생성
     * 
     * @param shortId QR 코드 단축 ID
     * @param request 방명록 생성 요청 정보
     * @return 생성된 방명록 정보
     */
    @PostMapping("/")
    // @RateLimit(plan = RateLimitPlan.GUESTBOOK_WRITE)
    public ResponseEntity<GuestbookDto.Response> createGuestbook(@PathVariable String shortId,
            @RequestBody GuestbookDto.Create request) {

        GuestbookDto.Response response = guestbookService.createGuestbook(shortId, request);
        return ResponseEntity.ok(response);
    }

    // /**
    // * 방명록 목록 조회
    // *
    // * @param shortId QR 코드 단축 ID
    // * @param page 페이지 번호
    // * @param size 페이지 크기
    // * @return 방명록 목록 응답
    // */
    // @GetMapping("/")
    // public ResponseEntity<GuestbookDto.ListResponse> getGuestbooks(@PathVariable
    // String shortId,
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "10") int size) {
    // GuestbookDto.ListResponse response = guestbookService.getGuestbooks(shortId,
    // page, size);
    // return ResponseEntity.ok(response);
    // }

    /**
     * 방명록 목록 조회
     * 
     * @param shortId         QR 코드 단축 ID
     * @param beforeCreatedAt 이전 생성 시간
     * @param beforeId        이전 ID
     * @param size            페이지 크기
     * @return 방명록 목록 응답
     */
    @GetMapping("/")
    public ResponseEntity<GuestbookDto.ListResponse> getGuestbooksByCursor(@PathVariable String shortId,
            @RequestParam(required = true) LocalDateTime beforeCreatedAt,
            @RequestParam(required = true) UUID beforeId,
            @RequestParam(defaultValue = "10") int size) {
        GuestbookDto.ListResponse response = guestbookService.getGuestbooksByCursor(shortId, beforeCreatedAt, beforeId,
                size);
        return ResponseEntity.ok(response);
    }

    /**
     * 방명록 실시간 구독 (SSE)
     * 
     * @param shortId   QR 코드 단축 ID
     * @param timeoutMs 선택: 연결 타임아웃(ms). 미지정 시 기본 10분.
     * @return SseEmitter
     */
    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeGuestbook(
            @PathVariable String shortId,
            @RequestHeader(value="Last-Event-ID", required=false) String lastEventIdHead,
            @RequestParam(name = "timeout", defaultValue = "600000") long timeoutMs,
            @RequestParam(value = "lastEventId", required = false) String lastEventIdParam,
            @RequestParam(value = "size", defaultValue = "100") int size
        ) {
        long timeout = (timeoutMs >= 0) ? timeoutMs : 600_000L; // 600초 = 10분 
        
        String topic = "guestbook:" + shortId;

        SseEmitter sseEmitter = sseService.subscribe(topic, timeout);

        // * NOTE
        // * 헤더에 Last-Event-ID는 브라우저단에서 event source 사용 시 자동으로 붙이는 것
        // * lastEventIdParam은 헤더가 유실되거나, 프록시 등등으로 없어질 수 있으니 안전하게 한 번 더 보내는 용도
        String lastEventId = (lastEventIdHead != null) ? lastEventIdHead : lastEventIdParam;
        
        if (lastEventId != null && CursorUtil.matchShortId(lastEventId, shortId)) {
            guestbookService.replay(topic, sseEmitter, lastEventId, shortId, size);
        }

        return sseEmitter;
    }
}
