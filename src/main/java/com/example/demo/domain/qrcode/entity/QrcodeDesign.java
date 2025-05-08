package com.example.demo.domain.qrcode.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.image.entity.Image;

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

    @Column(name = "error_correction_level", nullable = false)
    private String errorCorrectionLevel;

    @Column(name = "include_margin", nullable = false)
    private Boolean includeMargin;

    @Column(name = "background_color", nullable = false)
    private String backgroundColor;

    @Column(name = "point_color", nullable = false)
    private String pointColor;

    @Column(name = "size", nullable = false)
    private Integer size;

    @Column(name = "dot_type", nullable = false)
    private String dotType;

    // 로고 이미지 관련 정보는 nullable 처리
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "image_id", nullable = true)
    private Image logoImage;

    @Column(name = "logo_visual_size", nullable = true)
    private Integer logoVisualSize;

    @Column(name = "logo_visual_ratio", nullable = true)
    private Double logoVisualRatio;

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

    // @Builder 어노테이션 사용 시 모든 필드가 optional 처리되어 빌더 패턴 사용
    @Builder
    public QrcodeDesign(
            QrcodeEvent qrcodeEvent,
            String errorCorrectionLevel,
            Boolean includeMargin,
            String backgroundColor,
            String pointColor,
            Integer size,
            String dotType,
            Image logoImage,
            Integer logoVisualSize,
            Double logoVisualRatio) {
        this.qrcodeEvent = qrcodeEvent;
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.includeMargin = includeMargin;
        this.backgroundColor = backgroundColor;
        this.pointColor = pointColor;
        this.size = size;
        this.dotType = dotType;
        this.logoImage = logoImage;
        this.logoVisualSize = logoVisualSize;
        this.logoVisualRatio = logoVisualRatio;
    }
}
