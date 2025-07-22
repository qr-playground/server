package com.example.demo.domain.statistic.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.domain.statistic.dto.StatisticDto;
import com.example.demo.domain.statistic.entity.Statistic;
import com.example.demo.domain.statistic.repository.StatisticRepository;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.error.ErrorCode;
import com.example.demo.global.error.exception.CustomException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

        private final UserRepository userRepository;
        private final StatisticRepository statisticRepository;

        public StatisticDto.Response getQrcodeStatistic(int page, int size) {

                Page<User> users = userRepository
                                .findAll(PageRequest.of(page, size, Sort.by("phoneNumber").descending()));

                return StatisticDto.Response.fromEntity(users);
        }

        public StatisticDto.Response getQrcodeStatisticTotal() {
                Statistic statistic = statisticRepository.findFirstByOrderByCreatedAtDesc()
                                .orElseThrow(() -> new CustomException(ErrorCode.STATISTIC_NOT_FOUND));

                return StatisticDto.Response.fromEntity(statistic);
        }

        // @Scheduled(cron = "0 */5 * * * *")
        @Transactional
        public void scheduleStatisticRefresh() {
                log.info("[" + LocalDateTime.now() + "]" + "통계 갱신 스케줄링 작업을 시작합니다...");
                try {
                        statisticRepository.refreshStatistic();
                        log.info("[" + LocalDateTime.now() + "]" + "통계 갱신을 성공적으로 완료했습니다.");
                } catch (Exception e) {
                        log.error("통계 갱신 스케줄링 작업 중 오류가 발생했습니다.", e);
                }
        }

}
