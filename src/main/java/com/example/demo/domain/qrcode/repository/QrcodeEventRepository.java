package com.example.demo.domain.qrcode.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.qrcode.entity.QrcodeEvent;

public interface QrcodeEventRepository extends JpaRepository<QrcodeEvent, UUID> {

    Optional<QrcodeEvent> findByShortId(String shortId);
}
