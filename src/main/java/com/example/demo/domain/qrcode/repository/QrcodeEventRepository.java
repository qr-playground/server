package com.example.demo.domain.qrcode.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.User;

public interface QrcodeEventRepository extends JpaRepository<QrcodeEvent, UUID> {

    Optional<QrcodeEvent> findByShortId(String shortId);

    Page<QrcodeEvent> findAllByUserAndIsDeletedFalse(User user, Pageable pageable);
}
