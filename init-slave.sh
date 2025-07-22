#!/bin/bash
set -e

echo "=== PostgreSQL Slave 초기화 시작 ==="

# 1) initdb가 띄운 임시 서버를 빠르게 종료
echo "Stopping temporary initdb server..."
pg_ctl -D /var/lib/postgresql/data -m fast stop

# 2) 데이터 디렉터리 완전 초기화
echo "Clearing data directory..."
rm -rf /var/lib/postgresql/data/*

# 3) Master가 준비될 때까지 포트 체크
echo "Master 서버 대기 중..."
until pg_isready -h postgres-master -p 5432; do
  echo "Master 서버 준비 대기 중... (5초 후 재시도)"
  sleep 5
done

echo "Master 서버 준비 완료. 백업 진행..."

# 4) pg_basebackup -R 로 베이스백업 + 자동 recovery 설정
PGPASSWORD=repl_password pg_basebackup \
  -h postgres-master \
  -p 5432 \
  -U replicator \
  -D /var/lib/postgresql/data \
  -Fp -Xs -P -R

echo "백업 완료. 복제 및 PGroonga WAL 설정 진행..."

# 5) postgresql.conf 에 PGroonga WAL RM 모듈 로드 및 옵션 추가
cat >> /var/lib/postgresql/data/postgresql.conf << 'EOF'
# === Slave 전용 설정 ===
hot_standby = on
max_connections = 200
listen_addresses = '*'

# PGroonga WAL Resource Manager 로드
shared_preload_libraries = 'pg_stat_statements,pgroonga_wal_resource_manager'
# WAL RM 활성화 (Groonga 내부 객체를 WAL로 복제)
pgroonga.enable_wal_resource_manager = on
EOF

# 6) pg_basebackup -R 로 자동생성되지 않았다면 recovery 설정 보강
cat >> /var/lib/postgresql/data/postgresql.auto.conf << 'EOF'
primary_conninfo = 'host=postgres-master port=5432 user=replicator password=repl_password application_name=slave1'
primary_slot_name  = 'replication_slot_slave1'
EOF
touch /var/lib/postgresql/data/standby.signal

# 7) 권한 및 소유권 설정
chmod 600 /var/lib/postgresql/data/postgresql.conf \
         /var/lib/postgresql/data/postgresql.auto.conf
chown -R postgres:postgres /var/lib/postgresql/data

echo "=== PostgreSQL Slave 초기화 완료 ==="