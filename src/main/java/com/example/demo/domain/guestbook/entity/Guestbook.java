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
        @UniqueConstraint(columnNames = { "qrcode_event_short_id", "device_id" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guestbook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qrcode_event_short_id", referencedColumnName = "short_id", nullable = false)
    private QrcodeEvent qrcodeEvent;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = true)
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