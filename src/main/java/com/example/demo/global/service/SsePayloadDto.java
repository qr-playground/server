package com.example.demo.global.service;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.global.util.CursorUtil;

import lombok.Getter;

public class SsePayloadDto {

    public interface SsePayload {
        String getTopic();

        String getEventName();

        Object getPayload();

        String getEventId();
    }

    @Getter
    public static class GuestbookCreatedPayload implements SsePayload {
        private final String topic;
        private final String eventName = "guestbook-created";
        private final String eventId;
        private final Object payload;

        public GuestbookCreatedPayload(GuestbookDto.GuestbookCreatedSsePayload payload) {
            this.topic = "guestbook:" + payload.getShortId();
            this.eventId = CursorUtil.toSseEventId(payload.getCreatedAt(), payload.getId(), payload.getShortId());
            this.payload = payload;
        }
    }
}
