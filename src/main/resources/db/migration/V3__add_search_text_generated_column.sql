-- V3__add_search_text_generated_column.sql
-- 검색용 Generated Column 추가

-- QrcodeEvent 테이블에 검색용 Generated Column 추가
DO $$
BEGIN
    RAISE NOTICE '검색용 Generated Column 설정을 시작합니다.';
    
    -- QrcodeEvent 테이블에 검색용 Generated Column 추가
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'qrcode_event' AND column_name = 'search_text') THEN
        EXECUTE 'ALTER TABLE qrcode_event ADD COLUMN search_text TEXT 
                 GENERATED ALWAYS AS (COALESCE(title, '''') || '' '' || COALESCE(description, '''')) STORED';
        RAISE NOTICE 'Generated Column search_text 추가 완료 (title + description 통합)';
    ELSE
        RAISE NOTICE 'Generated Column search_text 이미 존재함';
    END IF;
    
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Generated Column 생성 중 오류 발생: %', SQLERRM;
        RAISE NOTICE '일반 컬럼 검색은 계속 사용 가능합니다.';
        -- 오류가 발생해도 마이그레이션 자체는 실패하지 않도록 함
END $$; 