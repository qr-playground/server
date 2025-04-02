package com.example.demo.global.util;

import com.example.demo.domain.user.service.CustomUserDetails;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) auth.getPrincipal();
        }
        return null;
    }

    public static UUID getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getUser().getId();
        }
        return null;
    }
}