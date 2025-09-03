package com.example.demo.global.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseEventListener {

    private final SseService sseService;

    /**
     * SsePayload 이벤트를 트랜잭션 커밋 후 비동기로 발행한다.
     * createGuestbook 등에서 publishEvent(new
     * SsePayloadDto.GuestbookCreatedPayload(...)) 호출 시 트리거됨.
     */
    @Async("sseExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSsePayload(SsePayloadDto.SsePayload payload) {
        try {
            sseService.publish(payload);
        } catch (Exception e) {
            log.error("[SSE][ASYNC] publish 실패: topic={} event={} id={}", payload.getTopic(), payload.getEventName(),
                    payload.getEventId(), e);
        }
    }
}
