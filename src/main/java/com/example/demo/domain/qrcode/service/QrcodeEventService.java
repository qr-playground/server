package com.example.demo.domain.qrcode.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.image.entity.Image;
import com.example.demo.domain.image.service.ImageService;
import com.example.demo.domain.qrcode.dto.QrcodeEventDto;
import com.example.demo.domain.qrcode.entity.QrcodeBenefit;
import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.repository.QrcodeEventRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrcodeEventService {

    private final QrcodeEventRepository qrcodeEventRepository;
    private final QrcodeDesignService qrcodeDesignService;
    private final QrcodeBenefitService qrcodeBenefitService;
    private final ImageService imageService;

    /**
     * QR 코드 생성
     */
    @Transactional
    public QrcodeEventDto.Response createQrcodeEvent(QrcodeEventDto.Create request, User user) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.save(request.toQrcodeEventEntity(user));
        Image logoImage = null;

        if (request.getLogoImageId() != null) {
            logoImage = imageService.findByIdInternal(request.getLogoImageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_LOGO_IMAGE_NOT_FOUND));
        }

        QrcodeDesign qrcodeDesign = request.toQrcodeDesignEntity(qrcodeEvent, logoImage);
        QrcodeBenefit qrcodeBenefit = request.toQrcodeBenefitEntity(qrcodeEvent);

        qrcodeDesignService.createQrcodeDesign(qrcodeDesign);
        qrcodeBenefitService.createQrcodeBenefit(qrcodeBenefit);

        qrcodeEvent.setQrcodeDesign(qrcodeDesign);
        qrcodeEvent.setQrcodeBenefit(qrcodeBenefit);

        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }

    /**
     * QR 코드 이벤트 조회 by shortId
     */
    @Transactional(readOnly = true)
    public QrcodeEventDto.Response getQrcodeEventByShortId(String shortId) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.findByShortId(shortId)
                .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND));
        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }

    /**
     * ! 🔒 Internal API — 컨트롤러에서 직접 호출하지 마세요.
     * 
     * @param shortId QR 코드 이벤트 조회 by shortId
     * @return QR 코드 이벤트 엔티티
     * @throws CustomException QRCODE_EVENT_ENTITY_NOT_FOUND
     */
    public QrcodeEvent findByShortIdInternal(String shortId) {
        // return qrcodeEventRepository.findByShortId(shortId);

        return qrcodeEventRepository.findByShortId(shortId)
                .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND));
    }

    /**
     * 사용자의 QR 코드 이벤트 조회
     */
    // @Transactional(readOnly = true)
    // ! 이 부분 페이지내이션 및 정렬 필터링 적용.
    public QrcodeEventDto.ListResponse getUserQrcodeEvents(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Page<QrcodeEvent> qrcodeEvents =
        // qrcodeEventRepository.findAllByUserAndIsDeletedFalse(user, pageable);
        Page<QrcodeEvent> qrcodeEvents = qrcodeEventRepository.findAllByUserWithDetails(user, pageable);

        return QrcodeEventDto.ListResponse.fromEntity(qrcodeEvents);
    }

    /**
     * QR 코드 이벤트 종료
     */
    @Transactional
    public QrcodeEventDto.Response terminateQrcodeEvent(String shortId, User user) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.findByShortId(shortId)
                .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND));

        if (!qrcodeEvent.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND);
        }

        qrcodeEvent.terminate();
        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }

    /**
     * QR 코드 이벤트 삭제
     */
    @Transactional
    public QrcodeEventDto.Response deleteQrcodeEvent(String shortId, User user) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.findByShortId(shortId)
                .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND));

        if (!qrcodeEvent.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND);
        }

        qrcodeEvent.delete();
        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }

}