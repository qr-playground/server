package com.example.demo.global.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class ReplicaConsistencyService {
    private final JdbcTemplate slaveJdbcTemplate;

    public ReplicaConsistencyService(
            @Qualifier("slaveJdbcTemplate") JdbcTemplate slaveJdbcTemplate) {
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    // true면 레플리카가 requiredLsn 이상까지 도달
    public boolean isReplicaCaughtUp(String requiredLsn) {
        // pg_lsn_cmp(lsn1, lsn2): 현재 레플리카 LSN >= 현재 요청 LSN 이면 0 이상 반환 -> true
        Integer cmp = slaveJdbcTemplate.queryForObject(
                "select pg_lsn_cmp(pg_last_wal_replay_lsn(), ?)", Integer.class, requiredLsn);
        return cmp != null && cmp >= 0;
    }
}