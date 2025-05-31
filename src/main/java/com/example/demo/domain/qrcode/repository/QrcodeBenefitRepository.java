package com.example.demo.domain.qrcode.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.qrcode.entity.QrcodeBenefit;

public interface QrcodeBenefitRepository extends JpaRepository<QrcodeBenefit, UUID> {

}
