
package com.example.demo.global.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

/**
 * Master-Slave 데이터베이스 라우팅 클래스
 * 
 * @Transactional(readOnly=true) -> Slave DB 사용
 * 
 * @Transactional (기본값) -> Master DB 사용
 */
@Slf4j
public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 현재 트랜잭션 상태에 따라 사용할 데이터소스 결정
     * 
     * @return "slave" (읽기 전용) 또는 "master" (기본값)
     */
    @Override
    protected Object determineCurrentLookupKey() {
        // 현재 트랜잭션이 읽기 전용인지 확인
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        boolean isSynchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();

        String dataSourceType = isReadOnly ? "slave" : "master";

        log.debug("라우팅 데이터소스 선택: {} (readOnly: {}, transactionActive: {}, synchronizationActive: {})", 
                dataSourceType, isReadOnly, isTransactionActive, isSynchronizationActive);

        return dataSourceType;
    }
}