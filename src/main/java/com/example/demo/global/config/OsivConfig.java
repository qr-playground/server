package com.example.demo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class OsivConfig {

    @Bean
    public OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor(EntityManagerFactory emf) {
        OpenEntityManagerInViewInterceptor i = new OpenEntityManagerInViewInterceptor();
        i.setEntityManagerFactory(emf);
        return i;
    }
}