package com.example.demo.domain.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.auth.entity.RefreshToken;
import com.example.demo.domain.user.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByRefreshTokenAndIsRevokedFalse(String refreshToken);
}
