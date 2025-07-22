-- V2__install_pgroonga_extension.sql
-- PGroonga 확장 설치

-- PGroonga 확장 설치
DO $$
BEGIN
    -- PGroonga 확장이 존재하는지 확인
    IF NOT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pgroonga') THEN
        RAISE NOTICE 'PGroonga 확장이 설치되지 않았습니다. 확장을 설치합니다.';
        
        -- PGroonga 확장 설치 시도
        EXECUTE 'CREATE EXTENSION IF NOT EXISTS pgroonga';
        EXECUTE 'CREATE EXTENSION IF NOT EXISTS pgroonga_wal_resource_manager';
        RAISE NOTICE 'PGroonga 확장 설치 완료';
    ELSE
        RAISE NOTICE 'PGroonga 확장이 이미 설치되어 있습니다.';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'PGroonga 확장 설치 중 오류 발생: %', SQLERRM;
        RAISE NOTICE '확장 없이도 일반 검색은 사용 가능합니다.';
        -- 오류가 발생해도 마이그레이션 자체는 실패하지 않도록 함
END $$; 