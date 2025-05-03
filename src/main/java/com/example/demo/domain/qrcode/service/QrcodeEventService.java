package com.example.demo.domain.qrcode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.qrcode.dto.QrcodeEventDto;
import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.repository.QrcodeDesignRepository;
import com.example.demo.domain.qrcode.repository.QrcodeEventRepository;
import com.example.demo.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QrcodeEventService {

    private final QrcodeEventRepository qrcodeEventRepository;
    private final QrcodeDesignRepository qrcodeDesignRepository;

    /**
     * QR 코드 생성
     */
    @Transactional
    public QrcodeEventDto.Response createQrcodeEvent(QrcodeEventDto.Create request, User user) {

        QrcodeEvent qrcodeEvent = qrcodeEventRepository.save(request.toEntity(user));
        qrcodeDesignRepository.save(request.toEntity(qrcodeEvent));

        return QrcodeEventDto.Response.fromEntity(qrcodeEvent);
    }
}