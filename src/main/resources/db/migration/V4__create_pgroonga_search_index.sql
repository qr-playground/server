-- V4__create_pgroonga_search_index.sql
-- PGroonga 전문 검색 인덱스 생성

-- Generated Column에 PGroonga 전문 검색 인덱스 생성
DO $$
BEGIN
    RAISE NOTICE 'PGroonga 전문 검색 인덱스 생성을 시작합니다.';
    
    -- Generated Column에 PGroonga 전문 검색 인덱스 생성
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_pgroonga_qrcode_event_search_text') THEN
        EXECUTE 'CREATE INDEX idx_pgroonga_qrcode_event_search_text ON qrcode_event USING pgroonga (search_text)';
        RAISE NOTICE 'Generated Column용 전문 검색 인덱스 idx_pgroonga_qrcode_event_search_text 생성 완료';
        RAISE NOTICE '단일 컬럼으로 title + description 통합 검색 지원';
    ELSE
        RAISE NOTICE '인덱스 idx_pgroonga_qrcode_event_search_text 이미 존재함';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'PGroonga 인덱스 생성 중 오류 발생: %', SQLERRM;
        RAISE NOTICE '일반 B-tree 인덱스는 정상적으로 사용 가능합니다.';
        -- 오류가 발생해도 마이그레이션 자체는 실패하지 않도록 함
END $$; 