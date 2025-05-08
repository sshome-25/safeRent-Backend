-- SafeRent 데이터베이스 대량 데이터 생성 스크립트
-- 각 테이블별 10만개 데이터 생성 및 사용자별 active assessment 3개 유지

-- 1. 먼저 roles 테이블에 기본 역할 데이터 추가
INSERT INTO roles (role_id, name) VALUES 
(1, 'ADMIN'),
(2, 'USER'),
(3, 'AGENT');

-- 2. 사용자 데이터 생성 (10만명)
-- 사용자 비밀번호는 모두 해시된 'password123'로 통일
DELIMITER $$
CREATE PROCEDURE generate_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_users INT DEFAULT 100000;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_users DO
        INSERT INTO users (email, password, nickname, role_id)
        VALUES (
            CONCAT('user', i, '@example.com'),
            SHA2('password123', 256),
            CONCAT('user', i),
            -- 대부분 일반 사용자, 일부 관리자와 중개인
            CASE
                WHEN i <= 100 THEN 1  -- 관리자
                WHEN i > 100 AND i <= 5000 THEN 3  -- 중개인
                ELSE 2  -- 일반 사용자
            END
        );
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 3. 10만개의 거래된 주택 데이터 생성 (SRID 4326 지정)
DELIMITER $$
CREATE PROCEDURE generate_traded_houses()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE batch_size INT DEFAULT 1000; -- 배치 크기
    DECLARE total_houses INT DEFAULT 100000;
    DECLARE total_batches INT;
    
    -- 총 배치 수 계산
    SET total_batches = CEILING(total_houses / batch_size);
    
    -- 각 배치별로 처리
    WHILE i < total_batches DO
        -- 배치 트랜잭션 시작
        START TRANSACTION;
        
        -- 다중 행 삽입 구문 (한 번에 1000개씩 삽입)
        SET @sql = CONCAT('
            INSERT INTO traded_houses (
                location, area, floor, built_year, transaction_date, price,
                sggCd, umdNm, jibun, cityNm, aptNm, aptDong
            )
            SELECT 
                ST_SRID(POINT(
                    126.8 + (RAND() * 0.4), -- 경도(126.8 ~ 127.2)
                    37.4 + (RAND() * 0.3)   -- 위도(37.4 ~ 37.7)
                ), 4326) AS location,
                25 + (RAND() * 60) AS area,
                FLOOR(1 + (RAND() * 30)) AS floor,
                1990 + FLOOR(RAND() * 33) AS built_year,
                DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 1825) DAY) AS transaction_date,
                10000 + FLOOR(RAND() * 290000) AS price,
                CONCAT("11", LPAD(FLOOR(RAND() * 999), 3, "0")) AS sggCd,
                ELT(FLOOR(RAND() * 25) + 1, 
                    "강남동", "서초동", "송파동", "강서동", "마포동", 
                    "용산동", "종로동", "중구동", "성북동", "광진동", 
                    "영등포동", "동작동", "관악동", "강동동", "노원동", 
                    "도봉동", "강북동", "중랑동", "성동동", "동대문동", 
                    "은평동", "서대문동", "양천동", "구로동", "금천동"
                ) AS umdNm,
                CONCAT(FLOOR(RAND() * 999) + 1, "-", FLOOR(RAND() * 999) + 1) AS jibun,
                "서울시" AS cityNm,
                CONCAT(
                    ELT(FLOOR(RAND() * 10) + 1, 
                        "래미안", "자이", "힐스테이트", "푸르지오", "롯데캐슬", 
                        "아이파크", "더샵", "우방", "e편한세상", "센트럴파크"
                    ),
                    " ",
                    ELT(FLOOR(RAND() * 10) + 1, 
                        "파크", "스카이", "센트럴", "그린", "블루", 
                        "레이크", "포레스트", "골드", "실버", "프라임"
                    ),
                    " ",
                    FLOOR(RAND() * 5) + 1,
                    "차"
                ) AS aptNm,
                CONCAT(FLOOR(RAND() * 999) + 101, "동") AS aptDong
            FROM 
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t1,
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t2,
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t3
            LIMIT ', batch_size * (i + 1), ' OFFSET ', batch_size * i);
            
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        -- 배치 커밋
        COMMIT;
        
        -- 다음 배치로 이동
        SET i = i + 1;
        
        -- 잠시 대기 (선택 사항)
        DO SLEEP(0.1);
    END WHILE;
END$$
DELIMITER ;

-- 4. 게시글 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_posts()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_posts INT DEFAULT 100000;
    DECLARE random_user_id BIGINT;
    DECLARE random_house_id BIGINT;
    DECLARE total_users INT;
    DECLARE total_houses INT;
    
    -- 사용자 및 주택 총 개수 확인
    SELECT COUNT(*) INTO total_users FROM users;
    SELECT COUNT(*) INTO total_houses FROM traded_houses;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_posts DO
        -- 랜덤 사용자 및 주택 선택
        SET random_user_id = FLOOR(1 + (RAND() * total_users));
        
        -- 80%는 주택 연결, 20%는 null (연결되지 않은 게시글)
        IF RAND() < 0.8 THEN
            SET random_house_id = FLOOR(1 + (RAND() * total_houses));
        ELSE
            SET random_house_id = NULL;
        END IF;
        
        INSERT INTO posts (
            user_id, traded_house_id, title, content, view_count,
            prefer_location, prefer_room_num, prefer_area, parking
        ) VALUES (
            random_user_id,
            random_house_id,
            CONCAT('게시글 제목 #', i, ' - ', CASE FLOOR(RAND() * 10)
                WHEN 0 THEN '아파트 추천 부탁드립니다'
                WHEN 1 THEN '이 지역 살기 좋나요?'
                WHEN 2 THEN '이 매물 어떤가요?'
                WHEN 3 THEN '전세 사기 걱정됩니다'
                WHEN 4 THEN '주변 환경 정보 공유해주세요'
                WHEN 5 THEN '이사 계획 중입니다'
                WHEN 6 THEN '가격 적절한가요?'
                WHEN 7 THEN '계약시 주의사항'
                WHEN 8 THEN '신축 아파트 정보'
                ELSE '부동산 투자 조언 부탁드립니다'
            END),
            CONCAT('게시글 내용 #', i, ' 입니다. 안전한 집에 살고 싶어요. ', 
                  '더 자세한 정보가 필요합니다. ', 
                  CASE FLOOR(RAND() * 5)
                      WHEN 0 THEN '학군이 좋은 곳을 찾고 있습니다.'
                      WHEN 1 THEN '역세권 위주로 알아보고 있어요.'
                      WHEN 2 THEN '주차 공간이 넉넉한 곳이 좋습니다.'
                      WHEN 3 THEN '조용한 동네를 선호합니다.'
                      ELSE '가성비 좋은 매물 추천 부탁드립니다.'
                  END),
            FLOOR(RAND() * 9999) + 1,  -- 조회수
            CASE FLOOR(RAND() * 25) + 1
                WHEN 1 THEN '강남동'
                WHEN 2 THEN '서초동'
                WHEN 3 THEN '송파동'
                WHEN 4 THEN '강서동'
                WHEN 5 THEN '마포동'
                WHEN 6 THEN '용산동'
                WHEN 7 THEN '종로동'
                WHEN 8 THEN '중구동'
                WHEN 9 THEN '성북동'
                WHEN 10 THEN '광진동'
                WHEN 11 THEN '영등포동'
                WHEN 12 THEN '동작동'
                WHEN 13 THEN '관악동'
                WHEN 14 THEN '강동동'
                WHEN 15 THEN '노원동'
                WHEN 16 THEN '도봉동'
                WHEN 17 THEN '강북동'
                WHEN 18 THEN '중랑동'
                WHEN 19 THEN '성동동'
                WHEN 20 THEN '동대문동'
                WHEN 21 THEN '은평동'
                WHEN 22 THEN '서대문동'
                WHEN 23 THEN '양천동'
                WHEN 24 THEN '구로동'
                ELSE '금천동'
            END,
            FLOOR(RAND() * 5) + 1,  -- 1~5개 방
            20 + (RAND() * 60),  -- 20평 ~ 80평
            CASE WHEN RAND() < 0.7 THEN 1 ELSE 0 END  -- 70% 확률로 주차 필요
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 5. 댓글 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_comments()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_comments INT DEFAULT 100000;
    DECLARE random_user_id BIGINT;
    DECLARE random_post_id BIGINT;
    DECLARE random_house_id BIGINT;
    DECLARE random_parent_id BIGINT;
    DECLARE total_users INT;
    DECLARE total_posts INT;
    DECLARE total_houses INT;
    DECLARE is_reply BOOLEAN;
    
    -- 사용자, 게시글, 주택 총 개수 확인
    SELECT COUNT(*) INTO total_users FROM users;
    SELECT COUNT(*) INTO total_posts FROM posts;
    SELECT COUNT(*) INTO total_houses FROM traded_houses;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_comments DO
        -- 랜덤 사용자 및 게시글 선택
        SET random_user_id = FLOOR(1 + (RAND() * total_users));
        SET random_post_id = FLOOR(1 + (RAND() * total_posts));
        
        -- 30%는 답글로 설정
        SET is_reply = (RAND() < 0.3 AND i > 10);
        
        IF is_reply THEN
            -- 이미 존재하는 댓글 중에서 부모 댓글 선택
            SET random_parent_id = FLOOR(1 + (RAND() * (i - 1)));
        ELSE
            SET random_parent_id = NULL;
        END IF;
        
        -- 20%는 주택 연결, 80%는 null
        IF RAND() < 0.2 THEN
            SET random_house_id = FLOOR(1 + (RAND() * total_houses));
        ELSE
            SET random_house_id = NULL;
        END IF;
        
        INSERT INTO comments (
            parent_comment_id, user_id, post_id, traded_house_id, content
        ) VALUES (
            random_parent_id,
            random_user_id,
            random_post_id,
            random_house_id,
            CONCAT('댓글 #', i, ' - ', 
                  CASE FLOOR(RAND() * 10)
                      WHEN 0 THEN '좋은 정보 감사합니다.'
                      WHEN 1 THEN '저도 같은 고민 중이었어요.'
                      WHEN 2 THEN '제 경험으로는 그 지역이 좋았습니다.'
                      WHEN 3 THEN '더 정보가 필요해 보입니다.'
                      WHEN 4 THEN '부동산 전문가의 조언이 필요해 보여요.'
                      WHEN 5 THEN '계약 전에 꼭 확인하세요.'
                      WHEN 6 THEN '그 지역 학군이 정말 좋습니다.'
                      WHEN 7 THEN '교통이 편리한 것이 장점이에요.'
                      WHEN 8 THEN '주변 시설이 잘 갖춰져 있어요.'
                      ELSE '추가 정보 공유해 드립니다.'
                  END,
                  IF(is_reply, ' (답글입니다)', '')
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 6. 즐겨찾기 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_favorites()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_favorites INT DEFAULT 100000;
    DECLARE random_user_id BIGINT;
    DECLARE random_house_id BIGINT;
    DECLARE total_users INT;
    DECLARE total_houses INT;
    
    -- 사용자 및 주택 총 개수 확인
    SELECT COUNT(*) INTO total_users FROM users;
    SELECT COUNT(*) INTO total_houses FROM traded_houses;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    -- 중복 방지를 위한 임시 테이블 생성
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_favorites (
        user_id BIGINT,
        traded_house_id BIGINT,
        PRIMARY KEY (user_id, traded_house_id)
    );
    
    WHILE i <= total_favorites DO
        -- 랜덤 사용자 및 주택 선택
        SET random_user_id = FLOOR(1 + (RAND() * total_users));
        SET random_house_id = FLOOR(1 + (RAND() * total_houses));
        
        -- 중복 방지를 위해 임시 테이블에 삽입 시도
        INSERT IGNORE INTO temp_favorites (user_id, traded_house_id)
        VALUES (random_user_id, random_house_id);
        
        SET i = i + 1;
    END WHILE;
    
    -- 임시 테이블의 데이터를 실제 테이블에 삽입
    INSERT INTO favorites (user_id, traded_house_id)
    SELECT user_id, traded_house_id FROM temp_favorites;
    
    -- 임시 테이블 삭제
    DROP TEMPORARY TABLE IF EXISTS temp_favorites;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 7. analysis 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_analysis()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_analysis INT DEFAULT 100000;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_analysis DO
        INSERT INTO analysis (summary, risk_degree)
        VALUES (
            CONCAT('분석 요약 #', i, ' - ', 
                  CASE FLOOR(RAND() * 5)
                      WHEN 0 THEN '전반적으로 안전한 매물입니다. 위험 요소가 발견되지 않았습니다.'
                      WHEN 1 THEN '약간의 위험 요소가 있으나 계약 진행에 큰 문제는 없습니다.'
                      WHEN 2 THEN '몇 가지 주의해야 할 점이 있습니다. 세부 내용을 확인하세요.'
                      WHEN 3 THEN '여러 위험 요소가 발견되었습니다. 계약 전 전문가 상담을 권장합니다.'
                      ELSE '심각한 위험 요소가 발견되었습니다. 계약을 재고하시기 바랍니다.'
                  END,
                  ' 본 분석은 입력된 계약서와 등기부등본을 기반으로 합니다.'
            ),
            FLOOR(RAND() * 10)  -- 0~9 위험도
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 8. 계약서 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_contracts()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_contracts INT DEFAULT 100000;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_contracts DO
        INSERT INTO contracts (analysis_id)
        VALUES (i);  -- 1:1 매핑
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 9. 계약서 파일 경로 데이터 생성 (계약서당 1~3개 파일)
DELIMITER $$
CREATE PROCEDURE generate_contract_file_paths()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_contracts INT DEFAULT 100000;
    DECLARE file_count INT;
    DECLARE j INT;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_contracts DO
        -- 각 계약서당 1~3개의 파일
        SET file_count = FLOOR(RAND() * 3) + 1;
        SET j = 1;
        
        WHILE j <= file_count DO
            INSERT INTO contract_file_paths (file_path, contract_id)
            VALUES (
                CONCAT('/storage/contracts/', i, '/', j, '.pdf'),
                i
            );
            
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 10. 진단 주택 데이터 생성 (10만개) (SRID 4326 지정)
DELIMITER $$
CREATE PROCEDURE generate_assessment_houses()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE batch_size INT DEFAULT 1000; -- 배치 크기
    DECLARE total_houses INT DEFAULT 100000;
    DECLARE total_batches INT;
    
    -- 총 배치 수 계산
    SET total_batches = CEILING(total_houses / batch_size);
    
    -- 각 배치별로 처리
    WHILE i < total_batches DO
        -- 배치 트랜잭션 시작
        START TRANSACTION;
        
        -- 다중 행 삽입 구문 (한 번에 1000개씩 삽입)
        SET @sql = CONCAT('
            INSERT INTO assessment_houses (
                location, price, market_price, area, floor,
                sggCd, umdNm, jibun, cityNm, aptNm, aptDong, risk_degree
            )
            SELECT 
                ST_SRID(POINT(
                    126.8 + (RAND() * 0.4), -- 경도(126.8 ~ 127.2)
                    37.4 + (RAND() * 0.3)   -- 위도(37.4 ~ 37.7)
                ), 4326) AS location,
                10000 + FLOOR(RAND() * 290000) AS price,
                10000 + FLOOR(RAND() * 290000) AS market_price,
                25 + (RAND() * 60) AS area,
                FLOOR(1 + (RAND() * 30)) AS floor,
                CONCAT("11", LPAD(FLOOR(RAND() * 999), 3, "0")) AS sggCd,
                ELT(FLOOR(RAND() * 25) + 1, 
                    "강남동", "서초동", "송파동", "강서동", "마포동", 
                    "용산동", "종로동", "중구동", "성북동", "광진동", 
                    "영등포동", "동작동", "관악동", "강동동", "노원동", 
                    "도봉동", "강북동", "중랑동", "성동동", "동대문동", 
                    "은평동", "서대문동", "양천동", "구로동", "금천동"
                ) AS umdNm,
                CONCAT(FLOOR(RAND() * 999) + 1, "-", FLOOR(RAND() * 999) + 1) AS jibun,
                "서울시" AS cityNm,
                CONCAT(
                    ELT(FLOOR(RAND() * 10) + 1, 
                        "래미안", "자이", "힐스테이트", "푸르지오", "롯데캐슬", 
                        "아이파크", "더샵", "우방", "e편한세상", "센트럴파크"
                    ),
                    " ",
                    ELT(FLOOR(RAND() * 10) + 1, 
                        "파크", "스카이", "센트럴", "그린", "블루", 
                        "레이크", "포레스트", "골드", "실버", "프라임"
                    ),
                    " ",
                    FLOOR(RAND() * 5) + 1,
                    "차"
                ) AS aptNm,
                CONCAT(FLOOR(RAND() * 999) + 101, "동") AS aptDong,
                FLOOR(RAND() * 10) AS risk_degree
            FROM 
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t1,
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t2,
                (SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
                 SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) AS t3
            LIMIT ', batch_size * (i + 1), ' OFFSET ', batch_size * i);
            
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        
        -- 배치 커밋
        COMMIT;
        
        -- 다음 배치로 이동
        SET i = i + 1;
        
        -- 잠시 대기 (선택 사항)
        DO SLEEP(0.1);
    END WHILE;
END$$
DELIMITER ;

-- 11. 등본 데이터 생성 (10만개)
DELIMITER $$
CREATE PROCEDURE generate_registers()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_registers INT DEFAULT 100000;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_registers DO
        INSERT INTO registers (analysis_id)
        VALUES (i);  -- 1:1 매핑
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 12. 등본 파일 경로 데이터 생성
DELIMITER $$
CREATE PROCEDURE generate_register_file_paths()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_registers INT DEFAULT 100000;
    DECLARE file_count INT;
    DECLARE j INT;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_registers DO
        -- 각 등본당 1~2개의 파일
        SET file_count = FLOOR(RAND() * 2) + 1;
        SET j = 1;
        
        WHILE j <= file_count DO
            INSERT INTO register_file_paths (file_path, register_id)
            VALUES (
                CONCAT('/storage/registers/', i, '/', j, '.pdf'),
                i
            );
            
            SET j = j + 1;
        END WHILE;
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 13. assessments 데이터 생성 (사용자별로 active 3개, 전체 10만개)
DELIMITER $$
이 오류가 발생한 가능한 원인은 다음과 같습니다:

assessment_houses 테이블에 데이터를 아직 삽입하지 않았거나 삽입에 실패했습니다.
generate_assessments() 프로시저가 존재하지 않는 ID를 참조하고 있습니다.

해결 방법

테이블 생성 및 데이터 삽입 순서 확인:
데이터 생성 스크립트에서 테이블 간 의존성을 고려하여 순서대로 데이터를 삽입해야 합니다.
sql-- 올바른 실행 순서 (예시)
CALL generate_assessment_houses();  -- 먼저 참조될 테이블의 데이터를 생성
CALL generate_analysis();           -- analysis 데이터 생성
CALL generate_contracts();          -- contracts 데이터 생성
CALL generate_registers();          -- registers 데이터 생성
CALL generate_assessments();        -- 마지막으로 assessments 데이터 생성

generate_assessments() 프로시저 수정:
assessment_house_id가 실제 존재하는 값을 참조하도록 수정합니다. 원래 스크립트에서 이 프로시저는 다음과 같이 되어 있을 것입니다:
sqlINSERT INTO assessments (
    completeness, user_id, contract_id, register_id, assessment_house_id, status
) VALUES (
    FLOOR(RAND() * 101),  -- 0~100% 완료율
    random_user_id,
    i,  -- 계약서 ID
    i,  -- 등본 ID
    i,  -- 진단 주택 ID
    random_status
);
여기서 i를 사용하여 assessment_house_id를 설정하고 있는데, 이것이 assessment_houses 테이블에 실제로 존재하는 ID인지 확인해야 합니다.
ID 범위 확인 및 조정:
assessment_houses 테이블에 몇 개의 레코드가 있는지 확인하고, assessments 테이블에 데이터를 삽입할 때 그 범위 내의 ID만 사용하도록 합니다.
sql-- assessment_houses의 ID 범위 확인
SELECT MIN(assessment_house_id), MAX(assessment_house_id) FROM assessment_houses;

-- 수정된 generate_assessments 프로시저
DELIMITER $$
CREATE PROCEDURE generate_assessments()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_assessments INT DEFAULT 100000;
    DECLARE total_users INT;
    DECLARE random_user_id BIGINT;
    DECLARE current_active_count INT;
    DECLARE random_status VARCHAR(10);
    DECLARE max_house_id INT;
    
    -- 사용자 총 개수 확인
    SELECT COUNT(*) INTO total_users FROM users;
    
    -- assessment_houses의 최대 ID 확인
    SELECT MAX(assessment_house_id) INTO max_house_id FROM assessment_houses;
    
    -- max_house_id가 NULL이면 오류
    IF max_house_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'assessment_houses 테이블에 데이터가 없습니다.';
    END IF;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    -- 사용자별 ACTIVE 상태 assessment 카운트 테이블 생성
    DROP TEMPORARY TABLE IF EXISTS temp_user_counts;
    CREATE TEMPORARY TABLE temp_user_counts (
        temp_user_id BIGINT PRIMARY KEY,
        temp_count INT DEFAULT 0
    );
    
    -- 모든 사용자에 대해 카운트 0으로 초기화
    INSERT INTO temp_user_counts (temp_user_id, temp_count)
    SELECT user_id, 0 FROM users;
    
    WHILE i <= total_assessments DO
        -- 랜덤 사용자 선택 (1부터 total_users까지)
        SET random_user_id = FLOOR(1 + (RAND() * total_users));
        
        -- 해당 사용자가 존재하는지 확인하고, 활성 상태 진단서 개수 가져오기
        SELECT temp_count INTO current_active_count 
        FROM temp_user_counts 
        WHERE temp_user_id = random_user_id;
        
        -- NULL이 아닌지 확인
        IF current_active_count IS NULL THEN
            SET current_active_count = 0;
        END IF;
        
        -- 해당 사용자의 활성 상태 진단서가 3개 미만이면 ACTIVE로, 아니면 INACTIVE로 설정
        IF current_active_count < 3 THEN
            SET random_status = 'ACTIVE';
            -- 활성 상태 진단서 개수 증가
            UPDATE temp_user_counts 
            SET temp_count = temp_count + 1 
            WHERE temp_user_id = random_user_id;
        ELSE
            SET random_status = 'INACTIVE';
        END IF;
        
        -- 유효한 user_id가 있는지 확인 후 삽입
        IF random_user_id IS NOT NULL AND random_user_id > 0 THEN
            INSERT INTO assessments (
                completeness, user_id, contract_id, register_id, assessment_house_id, status
            ) VALUES (
                FLOOR(RAND() * 101),  -- 0~100% 완료율
                random_user_id,
                i,  -- 계약서 ID
                i,  -- 등본 ID
                LEAST(i, max_house_id),  -- assessment_houses 범위 내에서만
                random_status
            );
        END IF;
        
        SET i = i + 1;
    END WHILE;
    
    -- 임시 테이블 삭제
    DROP TEMPORARY TABLE IF EXISTS temp_user_counts;
    
    -- 트랜잭션 커밋
    COMMIT;
END$$
DELIMITER ;

-- 14. 주변 시설 데이터 생성 (각 5만개씩)
-- 병원 데이터 (SRID 4326 지정)
DELIMITER $
CREATE PROCEDURE generate_hospitals()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_hospitals INT DEFAULT 50000;
    DECLARE lat DECIMAL(10, 8);
    DECLARE lng DECIMAL(11, 8);
    
    -- 서울 지역 중심 좌표 (대략적인 값)
    DECLARE min_lat DECIMAL(10, 8) DEFAULT 37.4;
    DECLARE max_lat DECIMAL(10, 8) DEFAULT 37.7;
    DECLARE min_lng DECIMAL(11, 8) DEFAULT 126.8;
    DECLARE max_lng DECIMAL(11, 8) DEFAULT 127.2;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_hospitals DO
        -- 랜덤 위치 생성 (서울 지역 내)
        SET lat = min_lat + (RAND() * (max_lat - min_lat));
        SET lng = min_lng + (RAND() * (max_lng - min_lng));
        
        INSERT INTO hospitals (location, name)
        VALUES (
            ST_SRID(POINT(lng, lat), 4326),  -- SRID 4326 명시
            CONCAT(
                CASE FLOOR(RAND() * 10) + 1
                    WHEN 1 THEN '서울'
                    -- 나머지 case문
                    ELSE '연세'
                END,
                ' ',
                CASE FLOOR(RAND() * 5) + 1
                    WHEN 1 THEN '대학교병원'
                    -- 나머지 case문
                    ELSE '병원'
                END,
                ' #',
                i
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$
DELIMITER ;

-- 지하철역 데이터 (SRID 4326 지정)
DELIMITER $
CREATE PROCEDURE generate_stations()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_stations INT DEFAULT 50000;
    DECLARE lat DECIMAL(10, 8);
    DECLARE lng DECIMAL(11, 8);
    
    -- 서울 지역 중심 좌표 (대략적인 값)
    DECLARE min_lat DECIMAL(10, 8) DEFAULT 37.4;
    DECLARE max_lat DECIMAL(10, 8) DEFAULT 37.7;
    DECLARE min_lng DECIMAL(11, 8) DEFAULT 126.8;
    DECLARE max_lng DECIMAL(11, 8) DEFAULT 127.2;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_stations DO
        -- 랜덤 위치 생성 (서울 지역 내)
        SET lat = min_lat + (RAND() * (max_lat - min_lat));
        SET lng = min_lng + (RAND() * (max_lng - min_lng));
        
        INSERT INTO stations (location, name)
        VALUES (
            ST_SRID(POINT(lng, lat), 4326),  -- SRID 4326 명시
            CONCAT(
                CASE FLOOR(RAND() * 25) + 1
                    WHEN 1 THEN '강남'
                    -- 나머지 case문
                    ELSE '금천'
                END,
                CASE FLOOR(RAND() * 5) + 1
                    WHEN 1 THEN ''
                    -- 나머지 case문
                    ELSE '대학교'
                END,
                ' 역 #',
                i
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$
DELIMITER ;

-- 학교 데이터 (SRID 4326 지정)
DELIMITER $
CREATE PROCEDURE generate_schools()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_schools INT DEFAULT 50000;
    DECLARE lat DECIMAL(10, 8);
    DECLARE lng DECIMAL(11, 8);
    
    -- 서울 지역 중심 좌표 (대략적인 값)
    DECLARE min_lat DECIMAL(10, 8) DEFAULT 37.4;
    DECLARE max_lat DECIMAL(10, 8) DEFAULT 37.7;
    DECLARE min_lng DECIMAL(11, 8) DEFAULT 126.8;
    DECLARE max_lng DECIMAL(11, 8) DEFAULT 127.2;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_schools DO
        -- 랜덤 위치 생성 (서울 지역 내)
        SET lat = min_lat + (RAND() * (max_lat - min_lat));
        SET lng = min_lng + (RAND() * (max_lng - min_lng));
        
        INSERT INTO schools (location, name)
        VALUES (
            ST_SRID(POINT(lng, lat), 4326),  -- SRID 4326 명시
            CONCAT(
                CASE FLOOR(RAND() * 25) + 1
                    WHEN 1 THEN '강남'
                    -- 나머지 case문
                    ELSE '금천'
                END,
                CASE FLOOR(RAND() * 4) + 1
                    WHEN 1 THEN ' 초등학교'
                    -- 나머지 case문
                    ELSE ' 대학교'
                END,
                ' #',
                i
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$
DELIMITER ;

-- 상점 데이터 (SRID 4326 지정)
DELIMITER $
CREATE PROCEDURE generate_stores()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_stores INT DEFAULT 50000;
    DECLARE lat DECIMAL(10, 8);
    DECLARE lng DECIMAL(11, 8);
    
    -- 서울 지역 중심 좌표 (대략적인 값)
    DECLARE min_lat DECIMAL(10, 8) DEFAULT 37.4;
    DECLARE max_lat DECIMAL(10, 8) DEFAULT 37.7;
    DECLARE min_lng DECIMAL(11, 8) DEFAULT 126.8;
    DECLARE max_lng DECIMAL(11, 8) DEFAULT 127.2;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_stores DO
        -- 랜덤 위치 생성 (서울 지역 내)
        SET lat = min_lat + (RAND() * (max_lat - min_lat));
        SET lng = min_lng + (RAND() * (max_lng - min_lng));
        
        INSERT INTO stores (location, name)
        VALUES (
            ST_SRID(POINT(lng, lat), 4326),  -- SRID 4326 명시
            CONCAT(
                CASE FLOOR(RAND() * 10) + 1
                    WHEN 1 THEN '이마트'
                    -- 나머지 case문
                    ELSE '노브랜드'
                END,
                ' ',
                CASE FLOOR(RAND() * 25) + 1
                    WHEN 1 THEN '강남'
                    -- 나머지 case문
                    ELSE '금천'
                END,
                '점 #',
                i
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$
DELIMITER ;

-- 공원 데이터 (SRID 4326 지정)
DELIMITER $
CREATE PROCEDURE generate_parks()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_parks INT DEFAULT 50000;
    DECLARE lat DECIMAL(10, 8);
    DECLARE lng DECIMAL(11, 8);
    
    -- 서울 지역 중심 좌표 (대략적인 값)
    DECLARE min_lat DECIMAL(10, 8) DEFAULT 37.4;
    DECLARE max_lat DECIMAL(10, 8) DEFAULT 37.7;
    DECLARE min_lng DECIMAL(11, 8) DEFAULT 126.8;
    DECLARE max_lng DECIMAL(11, 8) DEFAULT 127.2;
    
    -- 트랜잭션 시작
    START TRANSACTION;
    
    WHILE i <= total_parks DO
        -- 랜덤 위치 생성 (서울 지역 내)
        SET lat = min_lat + (RAND() * (max_lat - min_lat));
        SET lng = min_lng + (RAND() * (max_lng - min_lng));
        
        INSERT INTO parks (location, name)
        VALUES (
            ST_SRID(POINT(lng, lat), 4326),  -- SRID 4326 명시
            CONCAT(
                CASE FLOOR(RAND() * 25) + 1
                    WHEN 1 THEN '강남'
                    -- 나머지 case문
                    ELSE '금천'
                END,
                CASE FLOOR(RAND() * 5) + 1
                    WHEN 1 THEN ' 공원'
                    -- 나머지 case문
                    ELSE ' 생태공원'
                END,
                ' #',
                i
            )
        );
        
        SET i = i + 1;
    END WHILE;
    
    -- 트랜잭션 커밋
    COMMIT;
END$
DELIMITER ;

-- 15. 모든 생성 프로시저 실행
DELIMITER ;

-- 프로시저 실행
CALL generate_users();
CALL generate_traded_houses();
CALL generate_posts();
CALL generate_comments();
CALL generate_favorites();
CALL generate_analysis();
CALL generate_contracts();
CALL generate_contract_file_paths();
CALL generate_assessment_houses();
CALL generate_registers();
CALL generate_register_file_paths();
CALL generate_assessments();
CALL generate_hospitals();
CALL generate_stations();
CALL generate_schools();
CALL generate_stores();
CALL generate_parks();

-- 프로시저 삭제
DROP PROCEDURE IF EXISTS generate_users;
DROP PROCEDURE IF EXISTS generate_traded_houses;
DROP PROCEDURE IF EXISTS generate_posts;
DROP PROCEDURE IF EXISTS generate_comments;
DROP PROCEDURE IF EXISTS generate_favorites;
DROP PROCEDURE IF EXISTS generate_analysis;
DROP PROCEDURE IF EXISTS generate_contracts;
DROP PROCEDURE IF EXISTS generate_contract_file_paths;
DROP PROCEDURE IF EXISTS generate_assessment_houses;
DROP PROCEDURE IF EXISTS generate_registers;
DROP PROCEDURE IF EXISTS generate_register_file_paths;
DROP PROCEDURE IF EXISTS generate_assessments;
DROP PROCEDURE IF EXISTS generate_hospitals;
DROP PROCEDURE IF EXISTS generate_stations;
DROP PROCEDURE IF EXISTS generate_schools;
DROP PROCEDURE IF EXISTS generate_stores;
DROP PROCEDURE IF EXISTS generate_parks;



-- 검증: 사용자별 active assessment가 정확히 3개인지 확인
SELECT user_id, COUNT(*) as active_count 
FROM assessments 
WHERE status = 'ACTIVE' 
GROUP BY user_id 
HAVING active_count != 3;

-- 테이블별 데이터 수 확인
SELECT 'users' AS table_name, COUNT(*) AS row_count FROM users
UNION
SELECT 'traded_houses', COUNT(*) FROM traded_houses
UNION
SELECT 'posts', COUNT(*) FROM posts
UNION
SELECT 'comments', COUNT(*) FROM comments
UNION
SELECT 'favorites', COUNT(*) FROM favorites
UNION
SELECT 'analysis', COUNT(*) FROM analysis
UNION
SELECT 'contracts', COUNT(*) FROM contracts
UNION
SELECT 'contract_file_paths', COUNT(*) FROM contract_file_paths
UNION
SELECT 'assessment_houses', COUNT(*) FROM assessment_houses
UNION
SELECT 'registers', COUNT(*) FROM registers
UNION
SELECT 'register_file_paths', COUNT(*) FROM register_file_paths
UNION
SELECT 'assessments', COUNT(*) FROM assessments
UNION
SELECT 'hospitals', COUNT(*) FROM hospitals
UNION
SELECT 'stations', COUNT(*) FROM stations
UNION
SELECT 'schools', COUNT(*) FROM schools
UNION
SELECT 'stores', COUNT(*) FROM stores
UNION
SELECT 'parks', COUNT(*) FROM parks
ORDER BY table_name;
