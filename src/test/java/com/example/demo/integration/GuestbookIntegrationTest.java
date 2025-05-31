package com.example.demo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.guestbook.dto.GuestbookDto;
import com.example.demo.domain.guestbook.repository.GuestbookRepository;
import com.example.demo.domain.guestbook.service.GuestbookService;
import com.example.demo.domain.qrcode.dto.QrcodeEventDto;
import com.example.demo.domain.qrcode.entity.QrcodeEvent;
import com.example.demo.domain.qrcode.repository.QrcodeEventRepository;
import com.example.demo.domain.qrcode.service.QrcodeEventService;
import com.example.demo.domain.user.entity.Role;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class GuestbookIntegrationTest {

    @Autowired
    private QrcodeEventRepository qrcodeEventRepository;

    @Autowired
    private QrcodeEventService qrcodeEventService;

    @Autowired
    private GuestbookService guestbookService;

    @Autowired
    private GuestbookRepository guestbookRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 필요시 각 테스트 시작 전 초기화
    }

    @AfterEach
    void cleanUp() {
        // 외래키 제약 순서대로 삭제
        guestbookRepository.deleteAll();
        qrcodeEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void guestbook_등록_성공() {
        // 1. 테스트용 User 생성
        User user = User.builder()
                .phoneNumber("01012345678")
                .password("pw")
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        // 2. 테스트용 QrcodeEvent 생성
        QrcodeEventDto.Create eventCreate = QrcodeEventDto.Create.builder()
                .title("테스트 이벤트")
                .description("테스트 설명")
                .secretCode("secret")
                .entryStartAt(LocalDateTime.now())
                .entryEndAt(LocalDateTime.now().plusDays(1))
                .errorCorrectionLevel("L")
                .includeMargin(true)
                .backgroundColor("#FFFFFF")
                .pointColor("#000000")
                .size(300)
                .dotType("square")
                .maxAttendeeCount(10)
                .logoImageId(null)
                .build();
        QrcodeEventDto.Response eventResponse = qrcodeEventService.createQrcodeEvent(eventCreate, user);

        String shortId = eventResponse.getQrcodeInfo().getQrcodeEventInfo().getShortId();

        // 3. guestbook 등록 요청 생성
        GuestbookDto.Create guestbookCreate = GuestbookDto.Create.builder()
                .name("테스터")
                .deviceId("device123")
                .phoneNumber("01099998888")
                .build();

        // 4. guestbook 등록
        GuestbookDto.Response response = guestbookService.createGuestbook(shortId, guestbookCreate);

        // 5. 검증
        assertNotNull(response);
        assertEquals("테스터", response.getName());
        assertEquals("01099998888", response.getPhoneNumber());
        assertEquals(shortId, response.getShortId());
    }

    @Test
    void guestbook_동시성_테스트() throws InterruptedException {
        // 1. 테스트용 User 생성
        User user = User.builder()
                .phoneNumber("01012345678")
                .password("pw")
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        // 2. 테스트용 QrcodeEvent 생성 (최대 5명만 참여 가능)
        QrcodeEventDto.Create eventCreate = QrcodeEventDto.Create.builder()
                .title("동시성 이벤트")
                .description("동시성 설명")
                .secretCode("secret")
                .entryStartAt(LocalDateTime.now())
                .entryEndAt(LocalDateTime.now().plusDays(1))
                .errorCorrectionLevel("L")
                .includeMargin(true)
                .backgroundColor("#FFFFFF")
                .pointColor("#000000")
                .size(300)
                .dotType("square")
                .maxAttendeeCount(5)
                .build();
        QrcodeEventDto.Response eventResponse = qrcodeEventService.createQrcodeEvent(eventCreate, user);
        String shortId = eventResponse.getQrcodeInfo().getQrcodeEventInfo().getShortId();

        log.info("shortId: {}", shortId);

        int threadCount = 10; // 10명이 동시에 등록 시도
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    GuestbookDto.Create guestbookCreate = GuestbookDto.Create.builder()
                            .name("테스터" + idx)
                            .deviceId("device" + idx)
                            .phoneNumber("0109999" + String.format("%04d", idx))
                            .build();
                    guestbookService.createGuestbook(shortId, guestbookCreate);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.warn("동시성 등록 실패: {}", e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 모든 쓰레드가 동시에 시작
        startLatch.countDown();
        endLatch.await(5000000, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("successCount: {}", successCount.get());
        log.info("failCount: {}", failCount.get());

        // 성공은 5건, 실패는 5건이어야 함 (최대 인원 5명)
        assertEquals(5, successCount.get(), "성공 등록 수는 5건이어야 함");
        assertEquals(5, failCount.get(), "실패(인원초과) 등록 수는 5건이어야 함");
    }
}