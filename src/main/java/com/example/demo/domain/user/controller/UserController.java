package com.example.demo.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.user.dto.UserDto;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.user.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(userService.getUser(userDetails.getUser().getId()));
    }
}
