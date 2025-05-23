use safeRent;

-- 외래키 체크 해제
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 데이터 삭제
TRUNCATE TABLE register_file_paths;  -- registers 참조
TRUNCATE TABLE contract_file_paths;  -- contracts 참조
TRUNCATE TABLE assessment_houses;    -- assessments, traded_houses 참조
TRUNCATE TABLE analysis;             -- users, traded_houses 참조
TRUNCATE TABLE favorites;            -- users, traded_houses 참조
TRUNCATE TABLE comments;             -- users, posts, traded_houses 참조
TRUNCATE TABLE posts;                -- users, traded_houses 참조
TRUNCATE TABLE assessments;          -- contracts, registers, users 참조
TRUNCATE TABLE registers;            -- users, traded_houses 참조
TRUNCATE TABLE contracts;            -- users, traded_houses 참조
TRUNCATE TABLE traded_houses;        -- users 참조
TRUNCATE TABLE users;                -- roles 참조
TRUNCATE TABLE roles;                -- 최상위 부모

-- 외래키 체크 복구
SET FOREIGN_KEY_CHECKS = 1;

-- 권한 정보 삽입
INSERT INTO roles (role_id, name) VALUES 
(1, '관리자'),
(2, '일반 사용자'),
(3, '부동산 중개사');

-- 사용자 정보 삽입
INSERT INTO users (user_id, email, password, nickname, role_id) VALUES
(1, 'admin@saferent.com', SHA2('admin123', 256), '관리자', 1),
(2, 'user1@example.com', SHA2('password123', 256), '홍길동', 2),
(3, 'user2@example.com', SHA2('password123', 256), '김철수', 2),
(4, 'user3@example.com', SHA2('password123', 256), '이영희', 2),
(5, 'user4@example.com', SHA2('password123', 256), '박민준', 2),
(6, 'agent1@realestate.com', SHA2('agent123', 256), '강부동', 3),
(7, 'agent2@realestate.com', SHA2('agent123', 256), '최중개', 3);

-- 거래된 집 정보 삽입
INSERT INTO traded_houses (location, area, floor, built_year, transaction_date, price, sggCd, umdNm, jibun, cityNm, aptNm, aptDong) VALUES
(ST_GeomFromText('POINT(37.56667 126.97806)', 4326), 84.2, 5, 2010, '2023-05-15', 85000, '11110', '종로구', '1-1', '서울시', '스카이뷰', 'A동'),
(ST_GeomFromText('POINT(37.50722 127.04889)', 4326), 59.8, 10, 2015, '2023-06-10', 65000, '11680', '강남구', '102-3', '서울시', '한강파크', 'B동'),
(ST_GeomFromText('POINT(37.55139 126.93694)', 4326), 76.3, 8, 2005, '2023-04-22', 70000, '11440', '마포구', '45-2', '서울시', '강변타워', 'C동'),
(ST_GeomFromText('POINT(37.49361 127.02583)', 4326), 92.5, 12, 2018, '2023-07-05', 95000, '11680', '강남구', '523-1', '서울시', '블루힐스', 'A동'),
(ST_GeomFromText('POINT(37.51278 126.89167)', 4326), 68.1, 7, 2012, '2023-03-30', 55000, '11410', '서대문구', '76-3', '서울시', '그린아파트', 'D동');

-- 이후 95개 더 삽입
INSERT INTO traded_houses (location, area, floor, built_year, transaction_date, price, sggCd, umdNm, jibun, cityNm, aptNm, aptDong)
SELECT 
    ST_GeomFromText(CONCAT('POINT(', (37.4 + RAND() * 0.3), ' ', (126.5 + RAND() * 1), ')'), 4326), -- 서울 지역 대략적인 좌표 범위
    ROUND(40 + RAND() * 80, 1), -- 40~120 평수
    CEILING(RAND() * 25), -- 1~25층
    2000 + CEILING(RAND() * 23), -- 2000~2023년 준공
    DATE_ADD('2022-01-01', INTERVAL CEILING(RAND() * 540) DAY), -- 2022-01-01부터 약 1년 6개월 내 거래
    30000 + CEILING(RAND() * 100000), -- 3억~13억원 (만원 단위)
    CONCAT('11', LPAD(CEILING(RAND() * 999), 3, '0')), -- 임의의 지역코드
    ELT(CEILING(RAND() * 10), '강남구', '서초구', '송파구', '마포구', '영등포구', '용산구', '강서구', '성북구', '종로구', '중구'), -- 임의의 법정동
    CONCAT(CEILING(RAND() * 999), '-', CEILING(RAND() * 99)), -- 임의의 지번
    '서울시', -- 도시명
    ELT(CEILING(RAND() * 10), '푸른아파트', '한강뷰', '스카이타워', '그랜드빌', '파크힐스', '시티뷰', '리버사이드', '센트럴파크', '골든게이트', '로얄팰리스'), -- 임의의 아파트 이름
    CONCAT(CHAR(65 + CEILING(RAND() * 10) - 1), '동') -- A~J동
FROM 
    INFORMATION_SCHEMA.TABLES A,
    INFORMATION_SCHEMA.TABLES B
LIMIT 95;


-- 진단용 집 정보 삽입
INSERT INTO assessment_houses (location, price, market_price, area, floor, address, is_safe)
SELECT 
    ST_GeomFromText(CONCAT('POINT(', (37.4 + RAND() * 0.3), ' ', (126.5 + RAND() * 1), ')'), 4326),
    30000 + CEILING(RAND() * 100000),
    30000 + CEILING(RAND() * 100000),
    ROUND(40 + RAND() * 80, 1),
    CEILING(RAND() * 25),
    CONCAT(
        ELT(CEILING(RAND() * 10), '강남구', '서초구', '송파구', '마포구', '영등포구', '용산구', '강서구', '성북구', '종로구', '중구'),
        ' ',
        CEILING(RAND() * 999), '-', CEILING(RAND() * 99)
    ),
    IF(RAND() > 0.2, TRUE, FALSE) -- 80% 확률로 안전
FROM 
    INFORMATION_SCHEMA.TABLES A,
    INFORMATION_SCHEMA.TABLES B
LIMIT 100;


-- 게시글 정보 삽입
INSERT INTO posts (user_id, traded_house_id, title, content, view_count, prefer_location, prefer_room_num, prefer_area, is_park)
SELECT 
    (SELECT user_id FROM users ORDER BY RAND() LIMIT 1), -- 랜덤 사용자
    IF(RAND() > 0.3, (SELECT traded_house_id FROM traded_houses ORDER BY RAND() LIMIT 1), NULL), -- 70% 확률로 집과 연결
    CONCAT('집을 찾고 있어요 #', CEILING(RAND() * 100)), -- 제목
    CONCAT('안녕하세요! 저는 ', 
           ELT(CEILING(RAND() * 5), '신혼부부', '직장인', '학생', '부모님과 함께', '싱글라이프'), 
           '로, ', 
           ELT(CEILING(RAND() * 10), '강남구', '서초구', '송파구', '마포구', '영등포구', '용산구', '강서구', '성북구', '종로구', '중구'), 
           '에 위치한 집을 찾고 있습니다. 예산은 ', 
           CEILING(RAND() * 10) * 1000, '만원 정도이고, 교통이 편리했으면 좋겠습니다.'), -- 내용
    CEILING(RAND() * 1000) + 1, -- 1~1000 조회수
    ELT(CEILING(RAND() * 10), '강남구', '서초구', '송파구', '마포구', '영등포구', '용산구', '강서구', '성북구', '종로구', '중구'), -- 선호 위치
    CEILING(RAND() * 4) + 1, -- 2~5 방 개수
    ROUND(40 + RAND() * 60, 1), -- 40~100 평수
    ROUND(RAND()) -- 주차 여부
FROM 
    INFORMATION_SCHEMA.TABLES A,
    INFORMATION_SCHEMA.TABLES B
LIMIT 100;

-- 댓글 정보 삽입
INSERT INTO comments (parent_comment_id, user_id, post_id, traded_house_id, content)
SELECT 
    NULL, -- 부모 댓글 없음 (최상위 댓글)
    (SELECT user_id FROM users ORDER BY RAND() LIMIT 1), -- 랜덤 사용자
    (SELECT post_id FROM posts ORDER BY RAND() LIMIT 1), -- 랜덤 게시글
    IF(RAND() > 0.5, (SELECT traded_house_id FROM traded_houses ORDER BY RAND() LIMIT 1), NULL), -- 50% 확률로 집과 연결
    ELT(CEILING(RAND() * 5), 
        '이 지역은 교통이 편리해서 추천드려요!', 
        '최근에 이사갔는데 만족하고 있습니다.', 
        '주변에 상가가 많아서 생활하기 좋아요.', 
        '학군이 좋은 지역이에요.', 
        '공원이 가까워서 산책하기 좋습니다.')
FROM 
    INFORMATION_SCHEMA.TABLES A
LIMIT 50;

-- 대댓글 정보 삽입
INSERT INTO comments (parent_comment_id, user_id, post_id, traded_house_id, content)
SELECT 
    (SELECT comment_id FROM comments WHERE parent_comment_id IS NULL ORDER BY RAND() LIMIT 1), -- 랜덤 부모 댓글
    (SELECT user_id FROM users ORDER BY RAND() LIMIT 1), -- 랜덤 사용자
    c.post_id, -- 부모 댓글의 게시글 ID
    NULL, -- 대댓글에는 집 연결 없음
    ELT(CEILING(RAND() * 5), 
        '감사합니다! 도움이 되었어요.', 
        '더 자세한 정보 있으실까요?', 
        '저도 같은 생각이에요.', 
        '정말 그런가요? 참고하겠습니다.', 
        '추가 정보 감사합니다!')
FROM 
    comments c
WHERE 
    c.parent_comment_id IS NULL
LIMIT 50;

-- 즐겨찾기 정보 삽입
INSERT INTO favorites (user_id, traded_house_id)
SELECT 
    (SELECT user_id FROM users WHERE role_id != 1 ORDER BY RAND() LIMIT 1), -- 랜덤 일반 사용자
    (SELECT traded_house_id FROM traded_houses ORDER BY RAND() LIMIT 1) -- 랜덤 집
FROM 
    INFORMATION_SCHEMA.TABLES A
LIMIT 100;

-- 분석 정보 랜덤 데이터 100건 삽입
INSERT INTO analysis (
    overallAssessment,
    riskFactor1,
    solution1,
    riskFactor2,
    solution2
)
SELECT
    CONCAT(
        '이 거래는 ',
        ELT(FLOOR(1 + (RAND() * 3)), '일반적인 시세와 비슷합니다.', '시세보다 약간 높은 편입니다.', '시세보다 낮은 편입니다.'),
        ' 계약서 검토 결과 ',
        ELT(FLOOR(1 + (RAND() * 3)), '특별한 위험 요소는 발견되지 않았습니다.', '몇 가지 확인이 필요한 조항이 있습니다.', '주의가 필요한 내용이 포함되어 있습니다.')
    ) AS overallAssessment,
    ELT(FLOOR(1 + (RAND() * 4)), '위험요소 없음', '근저당권 설정', '소유자 변경 이력 있음', '임차인 거주 불명확') AS riskFactor1,
    ELT(FLOOR(1 + (RAND() * 4)), '별도의 해결방안 필요 없음', '근저당권 말소 확인 필요', '소유자 변경 사유 확인', '임차인 실거주 여부 확인') AS solution1,
    ELT(FLOOR(1 + (RAND() * 4)), '추가 위험요소 없음', '전세권 설정', '가압류 존재', '권리관계 확인 필요') AS riskFactor2,
    ELT(FLOOR(1 + (RAND() * 4)), '별도의 해결방안 필요 없음', '전세권 말소 확인 필요', '가압류 해소 필요', '권리관계 서류 확인 필요') AS solution2
FROM
    INFORMATION_SCHEMA.TABLES
LIMIT 100;

-- 계약서 정보 삽입
INSERT INTO contracts (analysis_id)
SELECT 
    analysis_id
FROM 
    analysis
LIMIT 100;

-- 계약서 파일 경로 삽입
INSERT INTO contract_file_paths (file_path, contract_id)
SELECT 
    CONCAT('/uploads/contracts/', contract_id, '_', DATE_FORMAT(NOW(), '%Y%m%d'), '.pdf'),
    contract_id
FROM 
    contracts
LIMIT 100;

-- 등본 정보 삽입
INSERT INTO registers (analysis_id)
SELECT 
    analysis_id
FROM 
    analysis
LIMIT 100;

-- 등본 파일 경로 삽입
INSERT INTO register_file_paths (file_path, register_id)
SELECT 
    CONCAT('/uploads/registers/', register_id, '_', DATE_FORMAT(NOW(), '%Y%m%d'), '.pdf'),
    register_id
FROM 
    registers
LIMIT 100;

-- 진단서 정보 삽입
INSERT INTO assessments (
    user_id,
    register_id,
    assessment_house_id
)
SELECT 
    (SELECT user_id FROM users WHERE role_id != 1 ORDER BY RAND() LIMIT 1), -- 랜덤 일반 사용자
    r.register_id, -- 순서대로 등본
    (SELECT assessment_house_id FROM assessment_houses ORDER BY RAND() LIMIT 1) -- 랜덤 진단 집
FROM 
    registers r
LIMIT 100;
