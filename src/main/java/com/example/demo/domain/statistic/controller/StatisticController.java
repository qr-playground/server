package com.example.demo.domain.statistic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.statistic.dto.StatisticDto;
import com.example.demo.domain.statistic.service.StatisticService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/statistic")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/qrcode/user/page")
    public ResponseEntity<StatisticDto.Response> getQrcodeStatisticPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        StatisticDto.Response response = statisticService.getQrcodeStatistic(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qrcode/user/total")
    public ResponseEntity<StatisticDto.Response> getQrcodeStatisticTotal() {
        StatisticDto.Response response = statisticService.getQrcodeStatisticTotal();
        return ResponseEntity.ok(response);
    }
}