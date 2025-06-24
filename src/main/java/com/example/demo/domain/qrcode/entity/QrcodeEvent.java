package com.example.demo.domain.qrcode.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.user.entity.User;
import com.example.demo.global.util.CodeGeneratorUtil;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qrcode_event", uniqueConstraints = {
        @UniqueConstraint(name = "unique_qrcode_event_short_id", columnNames = { "short_id" })
}, indexes = {
        @Index(name = "idx_qrcode_event_user_id", columnList = "user_id"),
        @Index(name = "idx_qrcode_event_created_at", columnList = "created_at"),
        @Index(name = "idx_qrcode_event_entry_end_at", columnList = "entry_end_at"),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrcodeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "short_id", nullable = false, length = 12)
    private String shortId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "qrcodeEvent", cascade = CascadeType.ALL)
    private List<Guestbook> guestbooks = new ArrayList<>();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "secret_code", nullable = true)
    private String secretCode;

    @Column(name = "entry_start_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime entryStartAt;

    @Column(name = "entry_end_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime entryEndAt;

    @Column(name = "is_entry_ended", nullable = false)
    private Boolean isEntryEnded;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "qrcodeEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QrcodeDesign qrcodeDesign;

    @OneToOne(mappedBy = "qrcodeEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private QrcodeBenefit qrcodeBenefit;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.shortId = CodeGeneratorUtil.generateShortId();
        this.isEntryEnded = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public QrcodeEvent(User user, String title, String description, String secretCode,
            LocalDateTime entryStartAt, LocalDateTime entryEndAt) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.secretCode = secretCode;
        this.entryStartAt = entryStartAt;
        this.entryEndAt = entryEndAt;

        // 기본값 설정
        this.isEntryEnded = false;
        this.isDeleted = false;
    }

    /**
     * QR 코드 이벤트 종료 시간 업데이트
     */
    public void terminate() {
        this.entryEndAt = LocalDateTime.now();
        this.isEntryEnded = true;
    }

    /**
     * QR 코드 이벤트 삭제
     */
    public void delete() {
        this.isDeleted = true;
    }

    public void setQrcodeDesign(QrcodeDesign qrcodeDesign) {
        this.qrcodeDesign = qrcodeDesign;
    }

    public void setQrcodeBenefit(QrcodeBenefit qrcodeBenefit) {
        this.qrcodeBenefit = qrcodeBenefit;
    }
}