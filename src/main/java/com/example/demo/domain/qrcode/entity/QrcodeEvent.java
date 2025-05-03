package com.example.demo.domain.qrcode.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qrcode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrcodeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "qrcodeEvent", cascade = CascadeType.ALL)
    private List<Guestbook> guestbooks = new ArrayList<>();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "group_code", nullable = true)
    private String groupCode;

    @Column(name = "entry_start_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime entryStartAt;

    @Column(name = "entry_end_at", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime entryEndAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "qrcodeEvent", cascade = CascadeType.ALL)
    private QrcodeDesign qrcodeDesign;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public QrcodeEvent(User user, String title, String description, String groupCode,
            LocalDateTime entryStartAt, LocalDateTime entryEndAt) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.groupCode = groupCode;
        this.entryStartAt = entryStartAt;
        this.entryEndAt = entryEndAt;
        this.isDeleted = false;
    }
}