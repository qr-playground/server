package com.example.demo.domain.guestbook.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.qrcode.entity.QrcodeEvent;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "guestbook", uniqueConstraints = {
        @UniqueConstraint(
                name = "unique_guestbook_short_id_device_id",
                columnNames = { "short_id", "device_id" }),
        @UniqueConstraint(
                name = "unique_guestbook_short_id_phone_number",
                columnNames = { "short_id", "phone_number" })
}, indexes = {
        @Index(name = "idx_guestbook_short_id", columnList = "short_id"),
        @Index(name = "idx_guestbook_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guestbook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_id", nullable = false, referencedColumnName = "short_id")
    private QrcodeEvent qrcodeEvent;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Builder
    public Guestbook(QrcodeEvent qrcodeEvent, String deviceId, String name, String phoneNumber) {
        this.qrcodeEvent = qrcodeEvent;
        this.deviceId = deviceId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}