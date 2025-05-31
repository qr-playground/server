package com.example.demo.domain.qrcode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.qrcode.entity.QrcodeDesign;
import com.example.demo.domain.qrcode.repository.QrcodeDesignRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrcodeDesignService {
    private final QrcodeDesignRepository qrcodeDesignRepository;

    @Transactional
    protected QrcodeDesign createQrcodeDesign(QrcodeDesign qrcodeDesign) {
        return qrcodeDesignRepository.save(qrcodeDesign);
    }

}
