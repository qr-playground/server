package com.example.demo.domain.qrcode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.image.entity.Image;
import com.example.demo.domain.image.service.ImageService;
import com.example.demo.domain.qrcode.dto.QrcodeEventDto;
import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.repository.QrcodeDesignRepository;
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
    private final QrcodeDesignRepository qrcodeDesignRepository;
    private final ImageService imageService;

    /**
     * QR 코드 생성
     */
    @Transactional
    public QrcodeEventDto.Response createQrcodeEvent(QrcodeEventDto.Create request, User user) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.save(request.toEntity(user));
        QrcodeDesign qrcodeDesign = request.toEntity(qrcodeEvent);

        if (request.getLogoImageId() != null) {
            Image logoImage = imageService.findByIdInternal(request.getLogoImageId());
            qrcodeDesign = request.toEntity(qrcodeEvent, logoImage);
        }

        qrcodeDesignRepository.save(qrcodeDesign);

        return QrcodeEventDto.Response.fromEntity(qrcodeEvent, qrcodeDesign);
    }

    @Transactional(readOnly = true)
    public QrcodeEventDto.Response getQrcodeEventByShortId(String shortId) {
        QrcodeEvent qrcodeEvent = qrcodeEventRepository.findByShortId(shortId)
                .orElseThrow(() -> new CustomException(ErrorCode.QRCODE_EVENT_ENTITY_NOT_FOUND));
        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }
}