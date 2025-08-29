package com.example.demo.global.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.demo.domain")
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    @Bean("masterDataSource")
    @ConfigurationProperties("spring.datasource.master.hikari")
    public DataSource masterDataSource() {

        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("slaveDataSource")
    @ConfigurationProperties("spring.datasource.slave.hikari")
    public DataSource slaveDataSource() {

        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource masterDataSource,
            @Qualifier("slaveDataSource") DataSource slaveDataSource) {

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave", slaveDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        // AbstractRoutingDataSource 초기화 (중요!)
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        // LazyConnectionDataSourceProxy로 래핑하여 실제 커넥션이 필요한 시점까지 DataSource 선택을 지연
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public JdbcTemplate masterJdbcTemplate(
            @Qualifier("masterDataSource") DataSource masterDataSource) {
        return new org.springframework.jdbc.core.JdbcTemplate(masterDataSource);
    }

    @Bean
    public JdbcTemplate slaveJdbcTemplate(
            @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        return new org.springframework.jdbc.core.JdbcTemplate(slaveDataSource);
    }
}
