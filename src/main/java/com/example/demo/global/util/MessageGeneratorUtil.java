package com.example.demo.global.util;

public class MessageGeneratorUtil {

    public static String generateVerificationCodeMessage(String verificationCode) {
        return "[QR-WORLD] 인증번호는 [" + verificationCode + "] 입니다. 인증번호는 10분 동안 유효합니다.";
    }
}
