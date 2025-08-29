package com.example.demo.global.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.global.util.CodeGeneratorUtil;
import com.example.demo.global.util.MessageGeneratorUtil;
import com.github.benmanes.caffeine.cache.Cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${spring.sms.api-key}")
    private String apiKey;
    @Value("${spring.sms.api-secret}")
    private String apiSecret;
    @Value("${spring.sms.provider}")
    private String smsProvider;
    @Value("${spring.sms.sender}")
    private String smsSender;

    private DefaultMessageService messageService;
    private final Cache<String, String> verificationCodeCache;
    private final Cache<String, Boolean> verifiedPhoneNumberCache;

    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(
                apiKey,
                apiSecret,
                smsProvider);
    }

    // verificationCodeCache 에 인증코드 저장
    public boolean sendVerificationCodeSms(String to) {
        String verificationCode = CodeGeneratorUtil.generateVerificationCode();
        Message message = new Message();
        // 발신번호 및 수신번호는 "-"없이 입력
        message.setFrom(smsSender);
        message.setTo(to);

        String text = MessageGeneratorUtil.generateVerificationCodeMessage(verificationCode);
        message.setText(text);

        verificationCodeCache.put(to, verificationCode);
        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
            return true;
        } catch (Exception e) {
            log.error("SMS 발송 실패 : {}", e.getMessage());
            return false;
        }
    }

    // verificationCodeCache 에 인증코드 검증
    public boolean verifyCode(String to, String code) {
        String cachedCode = verificationCodeCache.getIfPresent(to);
        if (cachedCode == null || !cachedCode.equals(code)) {
            return false;
        }

        verificationCodeCache.invalidate(to);
        verifiedPhoneNumberCache.put(to, true);
        return true;
    }

    // 문자 발송
    public boolean sendSms(String to, String text) {
        Message message = new Message();
        // 발신번호 및 수신번호는 "-"없이 입력
        message.setFrom(smsSender);
        message.setTo(to);
        message.setText(text);

        try {
            messageService.sendOne(new SingleMessageSendingRequest(message));
            return true;
        } catch (Exception e) {
            log.error("SMS 발송 실패 : {}", e.getMessage());
            return false;
        }
    }

    public void mockSendSms(String to, String text) {
        try {
            Thread.sleep(500); // 0.5초 동안 현재 스레드를 중지
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mock SMS 지연 중 오류 발생", e);
        }
    }
}