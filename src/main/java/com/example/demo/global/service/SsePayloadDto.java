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
        // * topic: 서버 내부 라우팅 키
        // * eventName: 클라이언트 라우팅 키 
        
        private final String topic;
        private final String eventName = "guestbook:created";
        private final String eventId;
        private final Object payload;

        public GuestbookCreatedPayload(GuestbookDto.GuestbookCreatedSsePayload payload) {
            this.topic = "guestbook:created:" + payload.getShortId();
            this.eventId = CursorUtil.toSseEventId(payload.getCreatedAt().toString(), payload.getId(), payload.getShortId());
            this.payload = payload;
        }
    }
}
