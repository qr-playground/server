package com.example.demo.domain.user.service;

import com.example.demo.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !user.getDeleted();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getDeleted();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !user.getDeleted();
    }

    @Override
    public boolean isEnabled() {
        return !user.getDeleted();
    }
}