package com.example.demo.global.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public final class CodeGeneratorUtil {
    private static final char[] ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    private static final char[] NUMERIC_CHARACTERS = "1234567890".toCharArray();
    private static final int SHORT_ID_LENGTH = 12;
    private static final int VERIFICATION_CODE_LENGTH = 6;

    public static String generateShortId() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                ALPHANUMERIC_CHARACTERS,
                SHORT_ID_LENGTH);
    }

    public static String generateVerificationCode() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NUMERIC_CHARACTERS,
                VERIFICATION_CODE_LENGTH);
    }
}