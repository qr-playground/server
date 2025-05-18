package com.example.demo.domain.guestbook.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.entity.Guestbook;
import com.example.demo.domain.guestbook.repository.GuestbookRepository;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.service.QrcodeEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestbookService {

    private final GuestbookRepository guestbookRepository;
    private final QrcodeEventService qrcodeEventService;

    /**
     * 방명록 생성
     * 
     * @param shortId QR 코드 단축 ID
     * @param request 방명록 생성 요청 정보
     * @return 생성된 방명록 정보
     */
    @Transactional
    public GuestbookDto.Response createGuestbook(String shortId, GuestbookDto.Create request) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);

        Guestbook guestbook = request.toEntity(qrcodeEvent);
        Guestbook savedGuestbook = guestbookRepository.save(guestbook);
        return GuestbookDto.Response.fromEntity(savedGuestbook);
    }

    public GuestbookDto.ListResponse getGuestbooks(String shortId, int page, int size) {
        QrcodeEvent qrcodeEvent = qrcodeEventService.findByShortIdInternal(shortId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Guestbook> guestbooks = guestbookRepository.findAllByQrcodeEvent(qrcodeEvent, pageable);
        
        return GuestbookDto.ListResponse.fromEntity(guestbooks.getContent(),
                GuestbookDto.ListResponse.PaginationInfo.builder()
                        .totalItems(guestbooks.getTotalElements())
                        .totalPages(guestbooks.getTotalPages())
                        .currentPage(guestbooks.getNumber())
                        .pageSize(guestbooks.getSize())
                        .build());
    }
}
