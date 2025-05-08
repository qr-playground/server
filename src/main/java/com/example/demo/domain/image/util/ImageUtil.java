package com.example.demo.domain.image.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class ImageUtil {
    private ImageUtil() {
    }

    public static String generateDateKey(String originalFilename) {
        String folder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString();
        return String.format("images/%s/%s-%s", folder, uuid, sanitizeFilename(originalFilename));
    }

    private static String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}
