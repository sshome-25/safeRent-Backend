-- 기존 데이터 삭제
DELETE FROM assessments;
DELETE FROM favorites;
DELETE FROM comments;
DELETE FROM posts;
DELETE FROM contract_file_paths;
DELETE FROM register_file_paths;
DELETE FROM contracts;
DELETE FROM registers;
DELETE FROM analysis;
DELETE FROM traded_houses;
DELETE FROM assessment_houses;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM hospitals;
DELETE FROM stations;
DELETE FROM schools;
DELETE FROM stores;
DELETE FROM parks;

-- 권한 정보 삽입
INSERT INTO roles (role_id, name) VALUES 
(1, '관리자'),
(2, '일반 사용자'),
(3, '부동산 중개사');

-- 사용자 정보 삽입
INSERT INTO users (email, password, nickname, role_id) VALUES
('admin@saferent.com', SHA2('admin123', 256), '관리자', 1),
('user1@example.com', SHA2('password123', 256), '홍길동', 2),
('user2@example.com', SHA2('password123', 256), '김철수', 2),
('user3@example.com', SHA2('password123', 256), '이영희', 2),
('user4@example.com', SHA2('password123', 256), '박민준', 2),
('agent1@realestate.com', SHA2('agent123', 256), '강부동', 3),
('agent2@realestate.com', SHA2('agent123', 256), '최중개', 3);

-- 거래된 집 정보 삽입
INSERT INTO traded_houses (location, area, floor, built_year, transaction_date, price, sggCd, umdNm, jibun, cityNm, aptNm, aptDong) VALUES
(POINT(126.97806, 37.56667), 84.2, 5, 2010, '2023-05-15', 85000, '11110', '종로구', '1-1', '서울시', '스카이뷰', 'A동'),
(POINT(127.04889, 37.50722), 59.8, 10, 2015, '2023-06-10', 65000, '11680', '강남구', '102-3', '서울시', '한강파크', 'B동'),
(POINT(126.93694, 37.55139), 76.3, 8, 2005, '2023-04-22', 70000, '11440', '마포구', '45-2', '서울시', '강변타워', 'C동'),
(POINT(127.02583, 37.49361), 92.5, 12, 2018, '2023-07-05', 95000, '11680', '강남구', '523-1', '서울시', '블루힐스', 'A동'),
(POINT(126.89167, 37.51278), 68.1, 7, 2012, '2023-03-30', 55000, '11410', '서대문구', '76-3', '서울시', '그린아파트', 'D동');

-- 이후 95개 더 삽입
INSERT INTO traded_houses (location, area, floor, built_year, transaction_date, price, sggCd, umdNm, jibun, cityNm, aptNm, aptDong)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
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
INSERT INTO assessment_houses (location, price, market_price, area, floor, sggCd, umdNm, jibun, cityNm, aptNm, aptDong)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    30000 + CEILING(RAND() * 100000), -- 3억~13억원 (만원 단위)
    30000 + CEILING(RAND() * 100000), -- 3억~13억원 (만원 단위)
    ROUND(40 + RAND() * 80, 1), -- 40~120 평수
    CEILING(RAND() * 25), -- 1~25층
    CONCAT('11', LPAD(CEILING(RAND() * 999), 3, '0')), -- 임의의 지역코드
    ELT(CEILING(RAND() * 10), '강남구', '서초구', '송파구', '마포구', '영등포구', '용산구', '강서구', '성북구', '종로구', '중구'), -- 임의의 법정동
    CONCAT(CEILING(RAND() * 999), '-', CEILING(RAND() * 99)), -- 임의의 지번
    '서울시', -- 도시명
    ELT(CEILING(RAND() * 10), '푸른아파트', '한강뷰', '스카이타워', '그랜드빌', '파크힐스', '시티뷰', '리버사이드', '센트럴파크', '골든게이트', '로얄팰리스'), -- 임의의 아파트 이름
    CONCAT(CHAR(65 + CEILING(RAND() * 10) - 1), '동') -- A~J동
FROM 
    INFORMATION_SCHEMA.TABLES A,
    INFORMATION_SCHEMA.TABLES B
LIMIT 100;

-- 게시글 정보 삽입
INSERT INTO posts (user_id, traded_house_id, title, content, view_count, prefer_location, prefer_room_num, prefer_area, parking)
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

-- 분석 정보 삽입
INSERT INTO analysis (summary, risk_degree)
SELECT 
    CONCAT('이 거래는 ', 
           ELT(CEILING(RAND() * 3), '일반적인 시세와 비슷합니다.', '시세보다 약간 높은 편입니다.', '시세보다 낮은 편입니다.'), 
           ' 계약서 검토 결과 ', 
           ELT(CEILING(RAND() * 3), '특별한 위험 요소는 발견되지 않았습니다.', '몇 가지 확인이 필요한 조항이 있습니다.', '주의가 필요한 내용이 포함되어 있습니다.')),
    CEILING(RAND() * 10) -- 0~10 위험도
FROM 
    INFORMATION_SCHEMA.TABLES A
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
INSERT INTO assessments (completeness, user_id, contract_id, register_id, assessment_house_id)
SELECT 
    CEILING(RAND() * 100), -- 0~100% 완료도
    (SELECT user_id FROM users WHERE role_id != 1 ORDER BY RAND() LIMIT 1), -- 랜덤 일반 사용자
    c.contract_id, -- 순서대로 계약서
    r.register_id, -- 순서대로 등본
    (SELECT assessment_house_id FROM assessment_houses ORDER BY RAND() LIMIT 1) -- 랜덤 진단 집
FROM 
    contracts c
JOIN 
    registers r ON c.analysis_id = r.analysis_id
LIMIT 100;

-- 편의시설 정보 삽입 (병원)
INSERT INTO hospitals (location, name)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    CONCAT(ELT(CEILING(RAND() * 5), '서울', '연세', '강남', '삼성', '국민'), ' ', ELT(CEILING(RAND() * 3), '병원', '의원', '메디컬센터'))
FROM 
    INFORMATION_SCHEMA.TABLES
LIMIT 20;

-- 편의시설 정보 삽입 (역)
INSERT INTO stations (location, name)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    CONCAT(ELT(CEILING(RAND() * 10), '강남', '서울', '잠실', '홍대입구', '여의도', '신촌', '종로', '명동', '석계', '성수'), '역')
FROM 
    INFORMATION_SCHEMA.TABLES
LIMIT 20;

-- 편의시설 정보 삽입 (학교)
INSERT INTO schools (location, name)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    CONCAT(ELT(CEILING(RAND() * 10), '서울', '한강', '강남', '미래', '중앙', '동서', '하늘', '푸른', '미소', '행복'), 
           ELT(CEILING(RAND() * 3), '초등학교', '중학교', '고등학교'))
FROM 
    INFORMATION_SCHEMA.TABLES
LIMIT 20;

-- 편의시설 정보 삽입 (상점)
INSERT INTO stores (location, name)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    CONCAT(ELT(CEILING(RAND() * 5), 'GS', 'CU', '세븐일레븐', '이마트24', '미니스톱'), ' ', 
           ELT(CEILING(RAND() * 10), '강남점', '서초점', '송파점', '마포점', '영등포점', '용산점', '강서점', '성북점', '종로점', '중구점'))
FROM 
    INFORMATION_SCHEMA.TABLES
LIMIT 20;

-- 편의시설 정보 삽입 (공원)
INSERT INTO parks (location, name)
SELECT 
    POINT(126.5 + RAND() * 1, 37.4 + RAND() * 0.3), -- 서울 지역 대략적인 좌표 범위
    CONCAT(ELT(CEILING(RAND() * 10), '한강', '남산', '서울숲', '올림픽', '북서울', '서래', '보라매', '여의도', '달빛', '청계천'), ' 공원')
FROM 
    INFORMATION_SCHEMA.TABLES
LIMIT 20;


