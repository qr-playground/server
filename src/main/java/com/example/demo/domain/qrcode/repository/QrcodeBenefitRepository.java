package com.example.demo.domain.qrcode.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.qrcode.entity.QrcodeBenefit;

public interface QrcodeBenefitRepository extends JpaRepository<QrcodeBenefit, UUID> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
            UPDATE qrcode_benefit
            SET    available_attendee_count = available_attendee_count - 1
            WHERE  id = :id
                AND  available_attendee_count > 0
            """, nativeQuery = true)
    int decreaseIfAvailable(UUID id);
}
