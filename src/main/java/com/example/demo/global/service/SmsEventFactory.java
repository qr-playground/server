package com.example.demo.global.service;

import com.example.demo.global.util.MessageGeneratorUtil;

public class SmsEventFactory {
    public static SmsEventDto createGuestbookEvent(String phoneNumber, String eventTitle) {
        String message = MessageGeneratorUtil.generateEventJoinCompleteMessage(phoneNumber, eventTitle);
        return new SmsEventDto(SmsEventType.GUESTBOOK_CREATE, phoneNumber, message);
    }

    public static SmsEventDto createVerificationEvent(String phoneNumber, String verificationCode) {
        String message = MessageGeneratorUtil.generateVerificationCodeMessage(verificationCode);
        return new SmsEventDto(SmsEventType.VERIFICATION_CODE, phoneNumber, message);
    }
}
