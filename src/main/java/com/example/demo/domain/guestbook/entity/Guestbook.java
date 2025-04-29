package com.example.demo.domain.guestbook.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.qrcode.entity.Qrcode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Entity
@Table(name = "guestbook", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "qrcode_id", "device_id" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guestbook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qrcode_id", nullable = false)
    private Qrcode qrcode;

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
    public Guestbook(Qrcode qrcode, String deviceId, String name, String phoneNumber) {
        this.qrcode = qrcode;
        this.deviceId = deviceId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}