-- Master DB 초기화 스크립트
-- 복제 사용자 생성
CREATE USER replicator WITH REPLICATION ENCRYPTED PASSWORD 'repl_password';

-- 복제 슬롯 생성
SELECT pg_create_physical_replication_slot('replication_slot_slave1');

-- 복제 사용자에게 데이터베이스 접근 권한 부여
GRANT CONNECT ON DATABASE qrworld TO replicator;

-- 설정 변경 사항 적용 (pg_hba.conf는 파일 마운트로 처리)
SELECT pg_reload_conf(); 

CREATE EXTENSION IF NOT EXISTS pgroonga;
CREATE EXTENSION IF NOT EXISTS pgroonga_wal_resource_manager;