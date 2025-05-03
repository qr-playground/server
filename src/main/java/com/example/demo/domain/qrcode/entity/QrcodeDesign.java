package com.example.demo.domain.qrcode.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qrcode_design")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QrcodeDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qrcode_event_id", nullable = false)
    private QrcodeEvent qrcodeEvent;

    @Column(name = "top_text", nullable = true)
    private String topText;

    @Column(name = "bottom_text", nullable = true)
    private String bottomText;

    @Column(name = "center_text", nullable = true)
    private String centerText;

    @Column(name = "top_font_size", nullable = true)
    private Integer topFontSize;

    @Column(name = "bottom_font_size", nullable = true)
    private Integer bottomFontSize;

    @Column(name = "border_thickness", nullable = true)
    private Integer borderThickness;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime updatedAt;

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
    public QrcodeDesign(QrcodeEvent qrcodeEvent, String topText, String bottomText,
            String centerText, Integer topFontSize, Integer bottomFontSize,
            Integer borderThickness) {
        this.qrcodeEvent = qrcodeEvent;
        this.topText = topText;
        this.bottomText = bottomText;
        this.centerText = centerText;
        this.topFontSize = topFontSize;
        this.bottomFontSize = bottomFontSize;
        this.borderThickness = borderThickness;
    }
}
