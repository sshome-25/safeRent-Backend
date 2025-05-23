
alter table assessments drop column completeness;

ALTER TABLE assessments DROP FOREIGN KEY fk_assessments_contracts;

alter table assessments drop column contract_id;

ALTER TABLE analysis
    CHANGE COLUMN summary overall_assessment TEXT,
    ADD COLUMN risk_factor1 TEXT AFTER overall_assessment,
    ADD COLUMN solution1 TEXT AFTER risk_factor1,
    ADD COLUMN risk_factor2 TEXT AFTER solution1,
    ADD COLUMN solution2 TEXT AFTER risk_factor2;

ALTER TABLE analysis
    DROP COLUMN risk_degree;

select * from assessment_houses;

desc assessment_houses;

ALTER TABLE users ALTER COLUMN role_id SET DEFAULT 2;

alter table assessment_houses drop column sggCd;
alter table assessment_houses drop column umdNm;
alter table assessment_houses drop column jibun;
alter table assessment_houses drop column cityNm;
alter table assessment_houses drop column aptNm;
alter table assessment_houses drop column aptDong;
alter table assessment_houses drop column risk_degree;

alter table assessment_houses add address VARCHAR(255) NOT NULL;

alter table assessment_houses add is_safe BOOLEAN NOT NULL;
