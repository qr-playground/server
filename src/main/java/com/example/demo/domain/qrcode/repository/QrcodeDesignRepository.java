package com.example.demo.domain.qrcode.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.qrcode.entity.QrcodeDesign;

public interface QrcodeDesignRepository extends JpaRepository<QrcodeDesign, UUID> {

}
