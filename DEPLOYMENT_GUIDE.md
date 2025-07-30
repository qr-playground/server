# 🚀 3개 인스턴스 분리 배포 가이드

## 📋 개요

이 프로젝트는 3개의 독립적인 인스턴스로 분리하여 배포할 수 있도록 구성되어 있습니다.

## 🏗️ 인스턴스 구성

### 1️⃣ **애플리케이션 서버 인스턴스**

- **파일**: `docker-compose.app.yml`
- **서비스**: Spring Boot 애플리케이션
- **포트**: 8080
- **네트워크**: app-network

### 2️⃣ **데이터베이스 인스턴스**

- **파일**: `docker-compose.db.yml`
- **서비스**: PostgreSQL Master/Slave
- **포트**: 5555 (Master), 5556 (Slave)
- **네트워크**: db-network

### 3️⃣ **모니터링 인스턴스**

- **파일**: `docker-compose.monitor.yml`
- **서비스**: Prometheus, Grafana, Postgres Exporter
- **포트**: 9090 (Prometheus), 3001 (Grafana), 9187 (Exporter)
- **네트워크**: monitor-network

## 🚀 배포 방법

### 전체 통합 배포 (단일 인스턴스)

```bash
# 모든 서비스를 한 번에 배포
docker-compose up -d
```

### 분리 배포 (3개 인스턴스)

#### 1. 데이터베이스 인스턴스에 배포

```bash
# DB 인스턴스에서 실행
docker-compose -f docker-compose.db.yml up -d
```

#### 2. 모니터링 인스턴스에 배포

```bash
# 모니터링 인스턴스에서 실행
docker-compose -f docker-compose.monitor.yml up -d
```

#### 3. 애플리케이션 서버 인스턴스에 배포

```bash
# 애플리케이션 인스턴스에서 실행
docker-compose -f docker-compose.app.yml up -d
```

## ⚙️ 환경 설정

### 공통 환경 변수 (.env)

각 인스턴스에서 동일한 `.env` 파일을 사용하되, **외부 연결 설정**을 수정해야 합니다.

#### 애플리케이션 서버 (.env)

```bash
# DB 연결 (외부 DB 인스턴스 IP로 변경)
SPRING_DATASOURCE_MASTER_JDBC_URL=jdbc:postgresql://DB_INSTANCE_IP:5555/qrworld
SPRING_DATASOURCE_SLAVE_JDBC_URL=jdbc:postgresql://DB_INSTANCE_IP:5556/qrworld

# 모니터링 연결 (외부 모니터링 인스턴스 IP로 변경)
PROMETHEUS_URL=http://MONITOR_INSTANCE_IP:9090
GRAFANA_URL=http://MONITOR_INSTANCE_IP:3001
```

#### 모니터링 서버 (.env)

```bash
# DB 연결 (외부 DB 인스턴스 IP로 변경)
DATA_SOURCE_NAME=postgresql://DB_INSTANCE_IP:5555/qrworld
```

## 🔧 네트워크 설정

### 방화벽 포트 개방

각 인스턴스에서 필요한 포트를 개방해야 합니다:

#### 애플리케이션 서버

- **8080**: 애플리케이션 접근용

#### 데이터베이스 서버

- **5555**: Master DB 접근용
- **5556**: Slave DB 접근용

#### 모니터링 서버

- **9090**: Prometheus 접근용
- **3001**: Grafana 접근용
- **9187**: Postgres Exporter 접근용

## 📊 모니터링 설정

### Prometheus 설정 (prometheus.yml)

외부 DB 인스턴스의 Postgres Exporter를 모니터링하도록 설정:

```yaml
scrape_configs:
  - job_name: "postgres-exporter"
    static_configs:
      - targets: ["DB_INSTANCE_IP:9187"]
```

### Grafana 설정

외부 Prometheus를 데이터 소스로 추가:

- URL: `http://MONITOR_INSTANCE_IP:9090`

## 🔍 문제 해결

### 1. 네트워크 연결 확인

```bash
# 각 인스턴스 간 연결 테스트
telnet DB_INSTANCE_IP 5555
telnet MONITOR_INSTANCE_IP 9090
```

### 2. 로그 확인

```bash
# 각 서비스별 로그 확인
docker-compose -f docker-compose.app.yml logs -f app
docker-compose -f docker-compose.db.yml logs -f postgres-master
docker-compose -f docker-compose.monitor.yml logs -f prometheus
```

### 3. 컨테이너 상태 확인

```bash
# 각 인스턴스에서 실행
docker-compose -f docker-compose.[service].yml ps
```

## 📝 주의사항

1. **환경 변수**: 각 인스턴스의 `.env` 파일에서 외부 IP 주소를 올바르게 설정
2. **네트워크 보안**: 필요한 포트만 개방하고, 보안 그룹 설정 확인
3. **데이터 백업**: DB 인스턴스의 볼륨 데이터 정기 백업
4. **모니터링**: 각 인스턴스의 리소스 사용량 모니터링

## 🔄 롤백 방법

문제 발생 시 전체 통합 배포로 롤백:

```bash
# 각 인스턴스에서 서비스 중지
docker-compose -f docker-compose.[service].yml down

# 단일 인스턴스에서 전체 서비스 시작
docker-compose up -d
```
