package com.example.demo.global.config;

public final class LsnContextHolder {
    private static final ThreadLocal<String> REQUIRED_MIN_LSN = new ThreadLocal<>();

    public static void setRequiredMinLsn(String lsn) {
        REQUIRED_MIN_LSN.set(lsn);
    }

    public static java.util.Optional<String> getRequiredMinLsn() {
        return java.util.Optional.ofNullable(REQUIRED_MIN_LSN.get());
    }

    public static void clear() {
        REQUIRED_MIN_LSN.remove();
    }
}