package com.example.demo.global.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SmsEventDto {
    private final SmsEventType eventType;
    private final String phoneNumber;
    private final String message;
}
