package com.example.demo.domain.qrcode.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.user.entity.User;

public interface QrcodeEventRepository extends JpaRepository<QrcodeEvent, UUID> {

    Optional<QrcodeEvent> findByShortId(String shortId);

    Page<QrcodeEvent> findAllByUserAndIsDeletedFalse(User user, Pageable pageable);

    @Query(value = "SELECT qe FROM QrcodeEvent qe " +
            "LEFT JOIN FETCH qe.qrcodeBenefit " +
            "LEFT JOIN FETCH qe.qrcodeDesign " +
            "WHERE qe.user = :user AND qe.isDeleted = false", countQuery = "SELECT COUNT(qe) FROM QrcodeEvent qe WHERE qe.user = :user AND qe.isDeleted = false")
    Page<QrcodeEvent> findAllByUserWithDetails(@Param("user") User user, Pageable pageable);
}
