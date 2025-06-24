package com.example.demo.domain.statistic.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.domain.statistic.dto.StatisticDto;
import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

        private final UserRepository userRepository;

        public StatisticDto.Response getQrcodeStatistic(int page, int size) {

                Page<User> users = userRepository
                                .findAll(PageRequest.of(page, size, Sort.by("phoneNumber").descending()));

                List<StatisticDto.UserQrcodeEventCount> userQrcodeEventCounts = users.getContent().stream()
                                .map(user -> new StatisticDto.UserQrcodeEventCount(
                                                user.getId(),
                                                user.getPhoneNumber(),
                                                user.getQrcodeEvents().size()))
                                .collect(Collectors.toList());

                return StatisticDto.Response.fromEntity(
                                userQrcodeEventCounts,
                                users.getTotalElements(),
                                users.getTotalPages(),
                                users.getNumber(),
                                users.getSize());
        }
}
