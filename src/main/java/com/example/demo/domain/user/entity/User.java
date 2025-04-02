package com.example.demo.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.domain.user.dto.AuthDto.Login;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "login_id", nullable = false, unique = true, length = 10)
    @Size(min = 4, max = 20, message = "Login ID는 최소 4자 이상, 최대 20자 이하이어야 합니다.")
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    @Size(min = 4, max = 100, message = "Password는 최소 4자 이상, 최대 20자 이하이어야 합니다.")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Builder
    public User(String name, String loginId, String password, Role role) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
    }
}
