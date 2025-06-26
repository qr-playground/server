package com.example.demo.domain.guestbook.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.guestbook.repository.GuestbookRepository;
import com.example.demo.domain.qrcode.entity.QrcodeBenefit;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.service.QrcodeBenefitService;
import com.example.demo.domain.qrcode.service.QrcodeEventService;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final QrcodeEventService qrcodeEventService;
    private final QrcodeBenefitService qrcodeBenefitService;

    private boolean isOpenQrcodeEvent(QrcodeEvent qrcodeEvent) {
        LocalDateTime now = LocalDateTime.now();

        log.info("qrcodeEvent.getEntryStartAt(): {}", qrcodeEvent.getEntryStartAt());
        log.info("qrcodeEvent.getEntryEndAt(): {}", qrcodeEvent.getEntryEndAt());
        log.info("now: {}", now);

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
        QrcodeBenefit qrcodeBenefit = qrcodeEvent.getQrcodeBenefit();

        // 참여가 가능한지 인원 체크 및 인원 감소
        Boolean isAvailable = qrcodeBenefitService.decreaseIfAvailableAttendeeCount(qrcodeBenefit.getId());
        if (!isAvailable) {
            throw new CustomException(ErrorCode.QRCODE_BENEFIT_ENTRY_ENDED);
        }

        Guestbook savedGuestbook = guestbookRepository.save(guestbook);
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
}
