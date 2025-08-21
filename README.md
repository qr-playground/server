## QR-World Server

### 기술 스택

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.1
- **ORM**: Spring Data JPA
- **Database**: PostgreSQL
- **Build**: Gradle

### 프로젝트 목적

- **배경**: 동아리 선착순 모집 시 노션 댓글로만 접수하여 불편하고 공정성 의심 발생
- **목표**: QR로 입장·등록하는 선착순 시스템을 구축하고, 순서를 정확하게 반영

### 한눈에 보는 문제/해결/결과/링크

- **이벤트 선착순 등록 성능 이슈**

  - **문제**: DB 병목, 커넥션 풀 고갈, 최대 TPS 10, 에러율 ~70%
  - **해결**: 트랜잭션 범위 최소화(Spring Event, @Async), HikariCP 튜닝, Primary-Replica 분리, 부하 테스트(K6)
  - **결과**: 최대 TPS 9.9 → 48.3, 에러율 70% → 0%
  - **관련 글**
    - [[QRworld] 알림 기능을 트랜잭션 밖으로! Event와 비동기 처리](https://velog.io/@suhwani/QRworld-%EB%93%B1%EB%A1%9D-%EC%84%B1%EA%B3%B5-%EC%8B%9C-%EC%95%8C%EB%A6%BC-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-with-Spring-Event)
    - [[QRworld] 과유불급, 적당한 게 최고다. HikariCP 최적화](https://velog.io/@suhwani/QRworld-HikariCP-%EC%84%A4%EC%A0%95%ED%95%98%EA%B8%B0)
    - [[QRworld] 남의 이벤트 때문에 내가 느려진다면? Replication](https://velog.io/@suhwani/QRworld-%EB%82%A8%EC%9D%98)

- **배포 안정성 및 비용 이슈**

  - **문제**: 배포 중 다운타임 위험, 문자 인증 외부 API 비용·남용 우려
  - **해결**: GitHub Actions + AWS ECR로 배포 자동화·롤백, Nginx Blue-Green 배포, Caffeine Cache 기반 Rate Limit(커스텀 어노테이션, IP+DeviceId)
  - **결과**: 배포 다운타임 ~1초 이내, 요청 남용·비용 증가 방지
  - **관련 글**
    - [[QRworld] 문자 인증도 다 돈이야. Caffeine Cache와 IP Rate Limit](https://velog.io/@suhwani/QRworld-%EB%AC%B8%EC%9E%90-%EC%9D%B8%EC%A6%9D-with-Caffeine-Cache-Rate-Limit)
    - [[QRworld] 업데이트마다 공지할 순 없어. 무중단 배포와 롤백, AWS](https://velog.io/@suhwani/123-l9ltq1jw)

- **통계/검색 쿼리 성능 이슈**
  - **문제**: 통계 쿼리 25.8초, 검색 3.3초로 UX 저하
  - **해결**: 통계 테이블 + 스케줄러(@Scheduled)로 사전 집계, Generated Column/PGroonga로 유사도 검색, 쿼리 플랜 개선(Seq → Index/Heap Scan)
  - **결과**: 통계 25.8초 → 15ms, 검색 3.3초 → 632ms
  - **관련 글**
    - [[QRworld] 서비스 통계를 보여줘. 통계 테이블과 스케줄러](https://velog.io/@suhwani/QRworld-%EC%BF%BC%EB%A6%AC-%EC%B5%9C%EC%A0%81%ED%99%94)
    - [[QRworld] 검색 기능, LIKE는 너무 느리잖아. PGroonga 활용 검색 최적화](https://velog.io/@suhwani/QRworld-%EA%B2%80%EC%83%89-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84-with-LIKE-%EC%97%86%EC%9D%B4)
