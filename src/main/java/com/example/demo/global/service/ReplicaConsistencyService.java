package com.example.demo.global.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Service
public class ReplicaConsistencyService {
    private final JdbcTemplate slaveJdbcTemplate;

    public ReplicaConsistencyService(
            @Qualifier("slaveJdbcTemplate") JdbcTemplate slaveJdbcTemplate) {
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    // true면 레플리카가 requiredLsn 이상까지 도달
    @Transactional(readOnly = true)
    public boolean isReplicaCaughtUp(String requiredLsn) {
        // pg_lsn_cmp(lsn1, lsn2): 현재 레플리카 LSN >= 현재 요청 LSN 이면 0 이상 반환 -> true
        log.info("[isReplicaCaughtUp][ENTER] requiredLsn={}", requiredLsn);
        Boolean ok = slaveJdbcTemplate.queryForObject(
                "select coalesce(pg_last_wal_replay_lsn(), '0/0'::pg_lsn) >= ?::pg_lsn",
                Boolean.class,
                requiredLsn);
        log.info("[isReplicaCaughtUp][EXIT] ok={}", ok);
        return Boolean.TRUE.equals(ok);
    }
}