package com.example.demo.global.service;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmsEventListener {

    private final SmsService smsService;

    // 트랜잭션 커밋 후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("asyncExecutor")
    public void handleSmsEvent(SmsEventDto event) {
        try {
            log.info("SMS 이벤트 처리 시작 - Thread: {}", Thread.currentThread().getName());
            smsService.mockSendSms(event.getPhoneNumber(), event.getMessage());
        } catch (Exception e) {
            log.error("SMS 발송 실패 : {}, phone number: {}, event type: {}", e.getMessage(), event.getPhoneNumber(),
                    event.getEventType(), e);
        }
    }
}
