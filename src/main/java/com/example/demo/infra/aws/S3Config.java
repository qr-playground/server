package com.example.demo.infra.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class S3Config {

    @Value("${cloud.aws.s3.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.s3.credentials.secret-key}")
    private String secretKey;
    @Value("${cloud.aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}