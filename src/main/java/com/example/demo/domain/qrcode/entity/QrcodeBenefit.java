package com.example.demo.domain.qrcode.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qrcode_benefit", indexes = {
        @Index(name = "idx_qrcode_benefit_qrcode_event_id", columnList = "qrcode_event_id"),
        @Index(name = "idx_qrcode_benefit_available_count", columnList = "available_attendee_count"),
        @Index(name = "idx_qrcode_benefit_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrcodeBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qrcode_event_id", nullable = false)
    private QrcodeEvent qrcodeEvent;

    @Column(name = "max_attendee_count", nullable = false)
    private Integer maxAttendeeCount;

    @Column(name = "available_attendee_count", nullable = false)
    private Integer availableAttendeeCount;

    // 참여 인원 제한 여부, availableAttendeeCount가 0이면 true
    @Column(name = "is_attendee_count_limited", nullable = false)
    private Boolean isAttendeeCountLimited;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isAttendeeCountLimited = false;
        this.availableAttendeeCount = maxAttendeeCount;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public QrcodeBenefit(QrcodeEvent qrcodeEvent, Integer maxAttendeeCount) {
        this.qrcodeEvent = qrcodeEvent;
        this.maxAttendeeCount = maxAttendeeCount;
    }

    public void incrementAvailableAttendeeCount() {
        this.availableAttendeeCount++;
    }

    public void decrementAvailableAttendeeCount() {
        this.availableAttendeeCount--;
    }

    public void setIsAttendeeCountLimited() {
        this.isAttendeeCountLimited = true;
    }
}