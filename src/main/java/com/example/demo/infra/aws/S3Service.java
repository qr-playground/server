package com.example.demo.infra.aws;

import java.io.InputStream;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3Service {

    private final S3Client s3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(S3Client s3) {
        this.s3 = s3;
    }

    /**
     * @param key         — 버킷 내 “가상 경로/파일명”
     * @param data        — 업로드할 스트림
     * @param size        — 바이트 크기
     * @param contentType — MIME 타입
     */
    public String upload(String key, InputStream data, long size, String contentType) {
        log.info("S3 업로드 시작: key={}, size={}, contentType={}", key, size, contentType);
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength(size)
                .acl(ObjectCannedACL.PUBLIC_READ) // 공개 읽기 권한이 필요하면
                .build();

        s3.putObject(req, RequestBody.fromInputStream(data, size));

        // SDK 유틸로 URL 생성
        return s3.utilities()
                .getUrl(b -> b.bucket(bucket).key(key))
                .toExternalForm();
    }

    /** 프리사인드 URL 생성 (만료 시간 지정) */
    public String generatePresignedUrl(String key, Duration validFor) {
        S3Presigner presigner = S3Presigner.create();
        PresignedGetObjectRequest preq = presigner.presignGetObject(r -> r.signatureDuration(validFor)
                .getObjectRequest(g -> g.bucket(bucket).key(key)));
        return preq.url().toExternalForm();
    }

    /** S3에서 객체 삭제 */
    public void delete(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }
}