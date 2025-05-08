USE safeRent;

-- 1. 매물 목록 조회: 반경 500m, 3개월 내 매물
SELECT 
    traded_house_id,
    CONCAT(aptNm, ' ', aptDong) AS name,
    price
FROM 
    traded_houses
WHERE 
    ST_Distance_Sphere(
        location,
        POINT(127.03, 37.50)
    ) <= 500;
    AND transaction_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH);

-- 2. 매물 상세 조회: 특정 매물 ID로 조회
SELECT 
    traded_house_id,
    CONCAT(aptNm, ' ', aptDong) AS name,
    price,
    area,
    built_year,
    floor,
    CONCAT(cityNm, ' ', umdNm, ' ', jibun) AS address
FROM 
    traded_houses
WHERE 
    traded_house_id = 1;


-- 3. 추천 매물 조회
-- 3-1. 특정 매물 위치 기준으로 반경 2KM 이내에 있는 인프라 수 반환
SELECT 
    COUNT(*) AS infra_count
FROM 
    hospitals
WHERE 
    ST_Distance_Sphere(
        location,
        POINT(127.03, 37.50)
    ) <= 2000;


-- 3-2. 특정 매물 위치 가장 가까운 인프라까지의 거리 반환
SELECT 
    ST_Distance_Sphere(
        location,
        POINT(127.03, 37.50)
    ) AS distance,
    name
FROM 
    hospitals
ORDER BY 
    distance ASC
LIMIT 1;

-- 4. 매물 관심목록 추가
INSERT INTO favorites (
    user_id,
    traded_house_id
) VALUES (
    1,
    1
);


-- SRID 4326 버전
--------------------------------------------------------------------------------------------------


-- 1. 매물 목록
SELECT 
    traded_house_id,
    CONCAT(aptNm, ' ', aptDong) AS name,
    price
FROM 
    traded_houses
WHERE 
    ST_DWithin(location, ST_PointFromText('POINT(127.03 37.50)', 4326), 500) -- 현재 중심위치 기준으로 반경 500m 이내의 매물을 선택
    AND
    transaction_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH); -- 3개월 이내의 매물을 선택




-- 2. 매물 상세 조회
-- name : 이름
-- price : 가격
-- area : 전용 면적
-- builtYear: 건축 일자
-- floor : 층
-- adress : 상세 주소 
SELECT 
    traded_house_id,
    CONCAT(aptNm, ' ', aptDong) AS name
    price,
    area,
    built_year,
    floor,
    CONCAT(cityNm, ' ', umdNm, ' ', jibun) AS address
FROM 
    traded_houses
WHERE 
  traded_house_id = 1; -- 특정 매물 ID로 조회




-- 3. 추천 매물 조회
-- 3-1. 특정 매물 위치 기준으로 반경 2KM 이내에 있는 인프라 수 반환
SELECT 
    COUNT(*) AS infra_count
FROM 
    hospitals   --${tableName}
WHERE 
    ST_DWithin(
        location,
        ST_PointFromText('POINT(127.03 37.50)', 4326), --ST_PointFromText(CONCAT('POINT(', #{longitude}, ' ', #{latitude}, ')'), 4326),
        2000
    ) 
  

-- 3-2. 특정 매물 위치 가장 가까운 인프라까지의 거리 반환
SELECT 
    ST_Distance_Sphere(
        location,
        ST_PointFromText('POINT(127.03 37.50)', 4326), --POINT(#{longitude}, #{latitude})
    ) AS distance,
    name
FROM 
    hospitals   --${tableName}
ORDER BY 
    distance ASC
LIMIT 1 

-- 4. 매물 관심목록 추가
INSERT INTO favorites (
    user_id,
    traded_house_id
) VALUES (
    1, -- 사용자 ID
    1  -- 매물 ID
);

