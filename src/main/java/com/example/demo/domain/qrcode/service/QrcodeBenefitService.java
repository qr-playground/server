package com.example.demo.domain.qrcode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.qrcode.entity.QrcodeBenefit;
import com.example.demo.domain.qrcode.repository.QrcodeBenefitRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrcodeBenefitService {

    private final QrcodeBenefitRepository qrcodeBenefitRepository;

    @Transactional
    protected QrcodeBenefit createQrcodeBenefit(QrcodeBenefit qrcodeBenefit) {
        return qrcodeBenefitRepository.save(qrcodeBenefit);
    }
}
