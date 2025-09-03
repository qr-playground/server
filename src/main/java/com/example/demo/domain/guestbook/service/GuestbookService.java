package com.example.demo.domain.guestbook.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.guestbook.repository.GuestbookRepository;
import com.example.demo.domain.qrcode.entity.QrcodeBenefit;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.service.QrcodeBenefitService;
import com.example.demo.domain.qrcode.service.QrcodeEventService;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;
import com.example.demo.global.service.SmsEventDto;
import com.example.demo.global.service.SmsEventFactory;
import com.example.demo.global.service.SmsService;
import com.example.demo.global.service.SsePayloadDto;
import com.example.demo.global.service.SseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestbookService {

    private final ApplicationEventPublisher eventPublisher;
    private final GuestbookRepository guestbookRepository;
    private final QrcodeEventService qrcodeEventService;
    private final QrcodeBenefitService qrcodeBenefitService;
    private final SmsService smsService;
    private final SseService sseService;
    private final GuestbookQueryService guestbookQueryService;

    private boolean isOpenQrcodeEvent(QrcodeEvent qrcodeEvent) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(qrcodeEvent.getEntryStartAt()) && now.isBefore(qrcodeEvent.getEntryEndAt());
    }

    /**
     * 방명록 생성
     * 
     * @param shortId QR 코드 단축 ID
     * @param request 방명록 생성 요청 정보
     * @return 생성된 방명록 정보
     */
    @Transactional
    public GuestbookDto.Response createGuestbook(String shortId, GuestbookDto.Create request) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);
        if (!isOpenQrcodeEvent(qrcodeEvent)) {
            throw new CustomException(ErrorCode.QRCODE_EVENT_ENTRY_NOT_OPEN);
        }

        Guestbook guestbook = request.toEntity(qrcodeEvent);

        String phoneNumber = guestbook.getPhoneNumber();
        String eventTitle = qrcodeEvent.getTitle();

        QrcodeBenefit qrcodeBenefit = qrcodeEvent.getQrcodeBenefit();

        // 참여가 가능한지 인원 체크 및 인원 감소
        Boolean isAvailable = qrcodeBenefitService.decreaseIfAvailableAttendeeCount(qrcodeBenefit.getId());
        if (!isAvailable) {
            throw new CustomException(ErrorCode.QRCODE_BENEFIT_ENTRY_ENDED);
        }

        Guestbook savedGuestbook = guestbookRepository.save(guestbook);

        // * SSE publish: 실시간 방명록 생성 이벤트
        GuestbookDto.GuestbookCreatedSsePayload ssePayload = GuestbookDto.GuestbookCreatedSsePayload
                .fromEntity(savedGuestbook);
        // sseService.publish(new SsePayloadDto.GuestbookCreatedPayload(ssePayload));
        eventPublisher.publishEvent(new SsePayloadDto.GuestbookCreatedPayload(ssePayload));

        // * NOTE: 알림 기능(문자 전송)
        SmsEventDto smsEvent = SmsEventFactory.createGuestbookEvent(phoneNumber, eventTitle);
        eventPublisher.publishEvent(smsEvent);

        // * NOTE: 모의 알림 기능(모의 문자 전송) (개선 전)
        // smsService.mockSendSms(phoneNumber,
        // MessageGeneratorUtil.generateEventJoinCompleteMessage(phoneNumber,
        // eventTitle));

        return GuestbookDto.Response.fromEntity(savedGuestbook);
    }

    /**
     * 방명록 목록 조회
     * 
     * @param shortId QR 코드 단축 ID
     * @param page    페이지 번호
     * @param size    페이지 크기
     * @return 방명록 목록 정보
     */
    public GuestbookDto.ListResponse getGuestbooks(String shortId, int page, int size) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Guestbook> guestbooks = guestbookRepository.findAllByQrcodeEvent(qrcodeEvent, pageable);

        return GuestbookDto.ListResponse.fromEntity(guestbooks);
    }

    /**
     * 커서 기반 목록 조회: 특정 커서(beforeCreatedAt, beforeId) 이전 항목을 size개 조회
     */
    public GuestbookDto.ListResponse getGuestbooksByCursor(String shortId, LocalDateTime beforeCreatedAt, UUID beforeId,
            int size) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Guestbook> list = guestbookRepository.findOlderThan(qrcodeEvent, beforeCreatedAt, beforeId, pageable);
        Page<Guestbook> pageAdapter = new PageImpl<>(list, pageable, 0);
        return GuestbookDto.ListResponse.fromEntity(pageAdapter);
    }

    public void replay(String topic, SseEmitter emitter, String lastEventId, String shortId, int size) {
        List<GuestbookDto.GuestbookCreatedSsePayload> payloads = guestbookQueryService.loadReplayPayloads(shortId,
                lastEventId, size);

        for (GuestbookDto.GuestbookCreatedSsePayload payload : payloads) {
            SsePayloadDto.GuestbookCreatedPayload ssePayload = new SsePayloadDto.GuestbookCreatedPayload(payload);
            try {
                emitter.send(SseEmitter.event()
                        .id(ssePayload.getEventId())
                        .name(ssePayload.getEventName())
                        .data(ssePayload.getPayload()));
            } catch (IOException e) {
                sseService.completeAndCleanup(ssePayload.getTopic(), emitter);
            }
        }
    }

}
