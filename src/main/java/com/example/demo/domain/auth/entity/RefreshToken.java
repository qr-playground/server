package com.example.demo.domain.auth.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime expiredAt;

    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked;
    
    @Column(name = "revoked_at", nullable = true, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime revokedAt;

    @PrePersist
    protected void onCreate() {
        isRevoked = false;
    }

    @Builder
    public RefreshToken(User user, String refreshToken, long refreshTokenValidityInSeconds) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = LocalDateTime.now().plusSeconds(refreshTokenValidityInSeconds);
    }
}
