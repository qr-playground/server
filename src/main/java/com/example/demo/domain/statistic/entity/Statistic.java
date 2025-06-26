package com.example.demo.domain.statistic.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "statistic", indexes = {
        @Index(name = "idx_statistic_created_at", columnList = "created_at")
})
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "total_user_count", nullable = false)
    private long totalUserCount;

    @Column(name = "total_qrcode_event_count", nullable = false)
    private long totalQrcodeEventCount;

    @Column(name = "total_guestbook_count", nullable = false)
    private long totalGuestbookCount;

    @Column(name = "avg_qrcode_events_per_user", nullable = false)
    private double avgQrcodeEventsPerUser;

    @Column(name = "avg_guestbooks_per_qrcode_event", nullable = false)
    private double avgGuestbooksPerQrcodeEvent;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Statistic(long totalUserCount, long totalQrcodeEventCount, long totalGuestbookCount,
            double avgQrcodeEventsPerUser, double avgGuestbooksPerQrcodeEvent) {
        this.totalUserCount = totalUserCount;
        this.totalQrcodeEventCount = totalQrcodeEventCount;
        this.totalGuestbookCount = totalGuestbookCount;
        this.avgQrcodeEventsPerUser = avgQrcodeEventsPerUser;
        this.avgGuestbooksPerQrcodeEvent = avgGuestbooksPerQrcodeEvent;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
