package com.example.demo.domain.guestbook.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.repository.GuestbookRepository;
import com.example.demo.global.util.CursorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestbookQueryService {

        private final GuestbookRepository guestbookRepository;

        @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
        public List<GuestbookDto.GuestbookCreatedSsePayload> loadReplayPayloads(
                        String shortId, String lastEventId, int size) {

                if (lastEventId == null) {
                        return List.of();
                }

                LocalDateTime afterCreatedAt = CursorUtil.getLocalDateTimeFromSseEventId(lastEventId);
                UUID afterId = CursorUtil.getIdFromSseEventId(lastEventId);

                Pageable pageable = PageRequest.of(0, size,
                                Sort.by(Sort.Direction.ASC, "createdAt").and(Sort.by(Sort.Direction.ASC, "id")));

                List<GuestbookDto.GuestbookCreatedSsePayload> list = guestbookRepository
                                .findAfterCursorProjection(shortId, afterCreatedAt, afterId, pageable);

                return list;
        }
}
