package com.example.demo.global.util;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CursorUtil {

    public static String toSseEventId(String createdAt, UUID id, String shortId) {
        return createdAt + "%" + id + "%" + shortId;
    }

    public static UUID getIdFromSseEventId(String eventId) {
        String[] parts = eventId.split("%");
        return UUID.fromString(parts[1].trim());
    }

    public static LocalDateTime getLocalDateTimeFromSseEventId(String eventId) {
        String[] parts = eventId.split("%");
        return LocalDateTime.parse(parts[0].trim());
    }

    public static String getShortIdFromSseEventId(String eventId) {
        String[] parts = eventId.split("%");
        return parts[2].trim();
    }

    public static boolean matchShortId(String lastEventId, String shortId) {
        String[] parts = lastEventId.split("%");
        return parts[2].trim().equals(shortId);
    }
}
