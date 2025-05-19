USE SAFERENT;

DROP PROCEDURE IF EXISTS migrate_house_data;

-- 저장 프로시저 생성
DELIMITER $$

CREATE PROCEDURE migrate_house_data()
BEGIN
    -- 변수 선언
    DECLARE batch_size INT DEFAULT 50000;
    DECLARE total_rows INT;
    DECLARE processed INT DEFAULT 0;
    DECLARE batches INT;
    DECLARE i INT DEFAULT 0;

    -- 성능 최적화 설정
    SET autocommit = 0;
    SET unique_checks = 0;
    SET foreign_key_checks = 0;
    SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';
    SET session wait_timeout = 28800; -- 8시간

    -- 임시 테이블 생성 (SSAFYHOME 데이터베이스에)
    DROP TEMPORARY TABLE IF EXISTS SSAFYHOME.temp_id_map;
    CREATE TEMPORARY TABLE SSAFYHOME.temp_id_map (
                                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                                     deal_id INT
    );

    -- 주택 거래 ID로 임시 테이블 채우기
    INSERT INTO SSAFYHOME.temp_id_map (deal_id)
    SELECT DISTINCT hd.no
    FROM SSAFYHOME.housedeals hd
             JOIN SSAFYHOME.houseinfos hi ON hd.apt_seq = hi.apt_seq
    WHERE hi.latitude IS NOT NULL AND hi.longitude IS NOT NULL;

    -- 전체 행 수와 배치 수 계산
    SELECT COUNT(*) INTO total_rows FROM SSAFYHOME.temp_id_map;
    SET batches = CEILING(total_rows / batch_size);

    -- 배치 처리 루프
    WHILE i < batches DO
            -- 현재 배치 데이터 삽입
            INSERT INTO SAFERENT.traded_houses (
                location, area, floor, built_year, transaction_date,
                price, sggCd, umdNm, jibun, cityNm, aptNm, aptDong
            )
            SELECT
                ST_SRID(
                        POINT(
                                CONVERT(NULLIF(TRIM(hi.longitude), ''), DECIMAL(10,6)),
                                CONVERT(NULLIF(TRIM(hi.latitude), ''), DECIMAL(10,6))
                        ),
                        4326
                ) AS location,
                hd.exclu_use_ar AS area,
                CASE
                    WHEN hd.floor REGEXP '^[0-9]+$' THEN CONVERT(hd.floor, SIGNED)
                    ELSE 1 -- 유효하지 않은 값은 1층으로 기본 설정
                    END AS floor,
                COALESCE(hi.build_year, 2000) AS built_year,
                STR_TO_DATE(CONCAT(
                                    COALESCE(hd.deal_year, YEAR(CURRENT_DATE())), '-',
                                    COALESCE(hd.deal_month, 1), '-',
                                    COALESCE(hd.deal_day, 1)
                            ), '%Y-%m-%d') AS transaction_date,
                CASE
                    WHEN hd.deal_amount REGEXP '[0-9]'
                        THEN CONVERT(REPLACE(REPLACE(hd.deal_amount, ',', ''), ' ', ''), UNSIGNED)
                    ELSE 0
                    END AS price,
                COALESCE(hi.sgg_cd, '00000') AS sggCd,
                COALESCE(hi.umd_nm, '') AS umdNm,
                COALESCE(hi.jibun, '') AS jibun,
                COALESCE(dc.gugun_name, hi.umd_nm, '') AS cityNm,
                COALESCE(hi.apt_nm, '') AS aptNm,
                COALESCE(hd.apt_dong, '') AS aptDong
            FROM SSAFYHOME.housedeals hd
                     JOIN SSAFYHOME.houseinfos hi ON hd.apt_seq = hi.apt_seq
                     LEFT JOIN SSAFYHOME.dongcodes dc ON LEFT(hi.sgg_cd, 5) = dc.dong_code
                     JOIN SSAFYHOME.temp_id_map tim ON hd.no = tim.deal_id
            WHERE tim.id BETWEEN (i * batch_size) + 1 AND (i + 1) * batch_size;

            -- 현재 배치 커밋
            COMMIT;

            -- 처리된 레코드 수 업데이트
            SET processed = processed + ROW_COUNT();
            SET i = i + 1;

            -- 진행 상황 로깅
            SELECT CONCAT('배치 처리 중: ', i, ' / ', batches, ' (',
                          ROUND((i / batches) * 100), '%)') AS Progress;
        END WHILE;

    -- 기본 설정 복원
    SET autocommit = 1;
    SET unique_checks = 1;
    SET foreign_key_checks = 1;

    -- 완료 보고
    SELECT CONCAT('마이그레이션 완료. SAFERENT.traded_houses로 ', processed, ' 건의 레코드가 이전되었습니다.') AS Result;
END$$

DELIMITER ;

-- 프로시저 실행
CALL migrate_house_data();