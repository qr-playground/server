package com.example.demo.domain.guestbook.service;

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

        Guestbook guestbook = request.toEntity(qrcodeEvent);

        QrcodeBenefit qrcodeBenefit = qrcodeEvent.getQrcodeBenefit();

        // 참여가 가능한지 인원 체크 
        if (qrcodeBenefit.getIsAttendeeCountLimited() && qrcodeBenefit.getAvailableAttendeeCount() <= 0) {
            throw new CustomException(ErrorCode.GUESTBOOK_QRCODE_EVENT_ENTRY_ENDED);
        }
        // TODO: 동시성, 락 구현 
        // 참여 인원 카운트 감소
        qrcodeBenefit.decrementAvailableAttendeeCount();
        if (qrcodeBenefit.getAvailableAttendeeCount() == 0) {
            // 참여 인원 제한 여부 설정
            qrcodeBenefit.setIsAttendeeCountLimited();
        }

        Guestbook savedGuestbook = guestbookRepository.save(guestbook);
        return GuestbookDto.Response.fromEntity(savedGuestbook);
    }

    /**
     * 방명록 목록 조회
     * 
     * @param shortId QR 코드 단축 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 방명록 목록 정보
     */
    public GuestbookDto.ListResponse getGuestbooks(String shortId, int page, int size) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Guestbook> guestbooks = guestbookRepository.findAllByQrcodeEvent(qrcodeEvent, pageable);
        
        return GuestbookDto.ListResponse.fromEntity(guestbooks);
    }
}
