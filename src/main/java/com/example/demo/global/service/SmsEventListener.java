package com.example.demo.global.service;

import java.util.concurrent.atomic.AtomicInteger;

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
    private final AtomicInteger smsSuccessCount = new AtomicInteger(0);
    private final AtomicInteger smsFailCount = new AtomicInteger(0);

    // 트랜잭션 커밋 후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("asyncExecutor")
    public void handleSmsEvent(SmsEventDto event) {
        try {
            log.info("✅✅✅ SMS 이벤트 처리 시작 - Thread: {}, 현재 성공: {}, 실패: {}",
                    Thread.currentThread().getName(), smsSuccessCount.get(), smsFailCount.get());
            // !! FixMe: 모의 발송 코드 제거 후 실제 발송 코드 추가

            smsService.mockSendSms(event.getPhoneNumber(), event.getMessage());
            log.info("✅✅✅ Mock SMS 발송 완료 - Thread: {}, 현재 성공: {}, 실패: {}",
                    Thread.currentThread().getName(), smsSuccessCount.get(), smsFailCount.get());

            // smsService.sendSms(event.getPhoneNumber(), event.getMessage());

            // SMS 발송 성공 시에만 성공 카운트 증가
            int currentSuccessCount = smsSuccessCount.incrementAndGet();
            log.info("🎉 SMS 발송 성공! - phone: {}, event: {}, 총 성공: {}, 총 실패: {}",
                    event.getPhoneNumber(), event.getEventType(), currentSuccessCount, smsFailCount.get());

        } catch (Exception e) {
            // SMS 발송 실패 시에만 실패 카운트 증가
            int currentFailCount = smsFailCount.incrementAndGet();
            log.error("❌❌❌ SMS 발송 실패 : {}, phone number: {}, event type: {}, 총 성공: {}, 총 실패: {}",
                    e.getMessage(), event.getPhoneNumber(), event.getEventType(), smsSuccessCount.get(),
                    currentFailCount);
        }
    }
}
