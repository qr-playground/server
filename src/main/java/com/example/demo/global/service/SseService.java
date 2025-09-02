package com.example.demo.global.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.global.service.SsePayloadDto.SsePayload;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SseService {

    // * 토픽
    // * NOTE: 읽기(브로드캐스트 시 순회)가 많고, 쓰기(구독 추가/제거)가 적은 SSE 특성에 맞는 자료구조
    // * add/remove 때 내부 배열을 복사하므로 쓰기가 잦으면 비용이 크다.
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> topicMap = new ConcurrentHashMap<>();

    // * 구독 시작
    public SseEmitter subscribe(String topic, long timeoutMillis) {
        SseEmitter emitter = new SseEmitter(timeoutMillis); // 0L 주면 타임아웃 없음

        // * 연결이 끊겼을 때 동작할 콜백 함수 등록
        emitter.onCompletion(() -> {
            removeEmitter(topic, emitter);
        });

        // * 연결이 타임아웃됐을 때 동작할 콜백 함수 등록
        emitter.onTimeout(() -> {
            completeAndCleanup(topic, emitter);
        });

        // * 연결이 에러가 발생했을 때 동작할 콜백 함수 등록
        emitter.onError(e -> {
            removeEmitter(topic, emitter);
        });

        // * 최초 연결 확인용 이벤트(선택)
        try {
            emitter.send(SseEmitter.event().name("ping").data("ok"));
        } catch (Exception e) {
            try {
                emitter.completeWithError(e);
            } catch (Exception ignore) {
            }
            removeEmitter(topic, emitter);
            return emitter;
        }

        // * topicMap에 해당 topic이 없다면 새로운
        topicMap.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    // * 이벤트 전송
    public void publish(SsePayload ssePayload) {
        String eventId = ssePayload.getEventId();
        String topic = ssePayload.getTopic();
        String eventName = ssePayload.getEventName();
        Object payload = ssePayload.getPayload();

        List<SseEmitter> list = topicMap.getOrDefault(topic, new CopyOnWriteArrayList<>());

        for (SseEmitter em : list) {
            try {
                em.send(SseEmitter.event()
                        .id(eventId)
                        .name(eventName)
                        .data(payload));
            } catch (Exception e) {
                try {
                    em.completeWithError(e);
                } catch (Exception ignore) {
                }
                removeEmitter(topic, em);

            }
        }
    }

    // * 리스트에서 emitter 제거
    private void removeEmitter(String topic, SseEmitter emitter) {
        List<SseEmitter> list = topicMap.get(topic);
        if (list != null) {
            list.remove(emitter);
        }
    }

    // * 연결 완료 후 정리 (complete 호출 + 리스트 제거)
    public void completeAndCleanup(String topic, SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception ignore) {
        } finally {
            removeEmitter(topic, emitter);
        }
    }

    // * replay는 각 도메인 service에서 구현
    // public void replay(String topic, SseEmitter emitter, String lastEventId) {
    // }
}