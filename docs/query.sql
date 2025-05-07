-- 회원가입
insert into users (email, password, nickname, role_id) 
values ('test@example.com', SHA2('test123', 256), '테스트 계정', 2);

-- 로그인
select nickname, role_id
from users
where email='admin@example.com' and password='' and status='ACTIVE';

-- 로그아웃 
update users set status='INACTIVE' where user_id=1;

-- 모든 회원 조회
SELECT * from users;

select * from assessment_houses;

select assessments.assessment_id
from assessments
where user_id=2;

-- 사용자별 진행중인 진단 조회 
SELECT 
    a.assessment_id,
    u.nickname AS user_nickname,
    ah.assessment_house_id,
    ah.aptNm AS apartment_name,
    ah.aptDong AS apartment_building,
    ah.area,
    ah.floor,
    ah.risk_degree AS house_risk_degree,
    an1.analysis_id AS contract_analysis_id,
    an1.risk_degree AS contract_risk_degree,
    an2.analysis_id AS register_analysis_id,
    an2.risk_degree AS register_risk_degree
FROM 
    assessments a
JOIN 
    users u ON a.user_id = u.user_id
JOIN 
    assessment_houses ah ON a.assessment_house_id = ah.assessment_house_id
JOIN 
    contracts c ON a.contract_id = c.contract_id
JOIN 
    registers r ON a.register_id = r.register_id
JOIN 
    analysis an1 ON c.analysis_id = an1.analysis_id
JOIN 
    analysis an2 ON r.analysis_id = an2.analysis_id
WHERE 
    a.status = 'ACTIVE'
ORDER BY 
    a.assessment_id DESC;

-- 진단서 출력
select house.cityNm, house.umdNm, house.jibun, house.aptDong, house.aptNm, house.risk_degree
from assessments
join assessment_houses as house on assessments.assessment_house_id = house.assessment_house_id
join registers on assessments.register_id = registers.register_id
join contracts on assessments.contract_id = contracts.contract_id
where assessment_id=1;

-- 사용자 매물 등기부 등본 진단 결과
SELECT     
    -- 등기부 등본 분석 정보
    r.register_id,
    an.analysis_id AS register_analysis_id,
    an.summary AS register_analysis_summary,
    an.risk_degree AS register_risk_degree,
    -- 첨부 파일 정보 (GROUP_CONCAT으로 여러 파일 경로 결합)
    GROUP_CONCAT(DISTINCT rp.file_path SEPARATOR '; ') AS register_files
FROM 
    assessments a
JOIN 
    registers r ON a.register_id = r.register_id
JOIN 
    analysis an ON r.analysis_id = an.analysis_id
LEFT JOIN 
    register_file_paths rp ON r.register_id = rp.register_id
WHERE 
    a.status = 'ACTIVE' and
    a.assessment_id = 1
ORDER BY 
    a.created_at DESC;

-- 사용지 매물 계약서 진단 결과
SELECT     
    -- 등기부 등본 분석 정보
    c.contract_id,
    an.analysis_id AS contract_analysis_id,
    an.summary AS contract_analysis_summary,
    an.risk_degree AS contract_risk_degree,
    -- 첨부 파일 정보 (GROUP_CONCAT으로 여러 파일 경로 결합)
    GROUP_CONCAT(DISTINCT cp.file_path SEPARATOR '; ') AS contract_files
FROM 
    assessments a
JOIN 
    contracts c ON a.contract_id = c.contract_id
JOIN 
    analysis an ON c.analysis_id = an.analysis_id
LEFT JOIN 
    contract_file_paths cp ON c.contract_id = cp.contract_path_id
WHERE 
    a.status = 'ACTIVE' and
    a.assessment_id = 1
ORDER BY 
    a.created_at DESC;

-- 매물 위치와 가격 입력 
INSERT INTO assessment_houses (
    location,
    price,
    market_price,
    area,
    floor,
    sggCd,
    umdNm,
    jibun,
    cityNm,
    aptNm,
    aptDong,
    risk_degree
) VALUES (
    ST_PointFromText('POINT(127.034012 37.503421)', 4326),  -- 위치 (경도, 위도)
    130000,            -- 사용자 매매가 (만원)
    135000,            -- 근처 시세 (만원)
    84.25,             -- 면적 (m²)
    15,                -- 층수
    '11680',           -- 지역코드 (강남구 예시)
    '역삼동',           -- 법정동
    '824-12',          -- 지번
    '서울특별시',        -- 시군구
    '역삼푸르지오',      -- 아파트 단지명
    '101동',           -- 아파트 동
    1                  -- 위험도 (0: 안전, 숫자가 클수록 위험)
);

SET @assessment_house_id = LAST_INSERT_ID(); -- 이거는 was에서 대체

INSERT INTO assessments (
    completeness,
    user_id,
    contract_id,
    register_id,
    assessment_house_id,
    status
) VALUES (
    10,                -- 진단 진행도 (%) - 초기 상태이므로 낮은 값
    1,                 -- 사용자 ID
    NULL,              -- 계약서 ID (아직 없음)
    NULL,              -- 등본 ID (아직 없음)
    @assessment_house_id,  -- 방금 생성한 house ID
    'ACTIVE'
);

-- 등기부 등본 등록

-- 계약서 등록







