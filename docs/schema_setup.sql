create database if not exists safeRent;
use safeRent;

DROP TABLE IF EXISTS assessments;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;

DROP TABLE IF EXISTS contract_file_paths;
DROP TABLE IF EXISTS register_file_paths;
DROP TABLE IF EXISTS contracts;
DROP TABLE IF EXISTS registers;
DROP TABLE IF EXISTS analysis;
DROP TABLE IF EXISTS traded_houses;
DROP TABLE IF EXISTS assessment_houses;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE `roles` (
                         `role_id` TINYINT NOT NULL PRIMARY KEY COMMENT '권한 id',
                         `name`	varchar(255)	NOT NULL COMMENT '권한 이름'
);

INSERT INTO `roles` (`role_id`, `name`) values (1, 'admin');
INSERT INTO `roles` (`role_id`, `name`) values (2, 'user');

CREATE TABLE `users` (
                         `user_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '유저 id',
                         `email`	varchar(255) UNIQUE NOT NULL COMMENT '유저 이메일',
                         `password`	varchar(255) NOT NULL COMMENT '유저 비밀번호, SHA 해싱',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                         `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '유저 활성 상태',
                         `nickname`	varchar(255) UNIQUE NOT NULL COMMENT '유저 닉네임',
                         `role_id`	TINYINT	NOT NULL COMMENT '유저 권한',
                         CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles`(`role_id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `traded_houses` (
                                 `traded_house_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '집 id',
                                 `location` POINT NOT NULL SRID 4326 COMMENT '위치',
                                 `area` DECIMAL(6,3) UNSIGNED NOT NULL COMMENT '평수',
                                 `floor` TINYINT NOT NULL COMMENT '층수',
                                 `built_year` SMALLINT UNSIGNED NOT NULL COMMENT '준공 년',
                                 `transaction_date` Date NOT NULL COMMENT '거래일자',
                                 `price` INT UNSIGNED NOT NULL COMMENT '가격 (만원 단위)',
                                 `sggCd` VARCHAR(255) NOT NULL COMMENT '지역코드',
                                 `umdNm` VARCHAR(255) NOT NULL COMMENT '법정동',
                                 `jibun` VARCHAR(255) NOT NULL COMMENT '지번',
                                 `cityNm` VARCHAR(255) NOT NULL COMMENT '시군구',
                                 `aptNm` VARCHAR(255) NOT NULL COMMENT '아파트 단지명',
                                 `aptDong` VARCHAR(255) NOT NULL COMMENT '아파트 동',
                                 SPATIAL INDEX idx_location (location)
);

CREATE TABLE `posts` (
                         `post_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 id',
                         `user_id`	BIGINT	NOT NULL COMMENT '게시글 user_id',
                         `traded_house_id`	BIGINT	NULL COMMENT '게시글 house_id',
                         `title`	varchar(255)	NOT NULL COMMENT '제목',
                         `content`	text NOT NULL COMMENT '본문',
                         `view_count` INTEGER UNSIGNED NOT NULL DEFAULT 0 COMMENT '조회수',
                         `prefer_location`	VARCHAR(255)	NOT NULL COMMENT '선호 위치 법정동',
                         `prefer_room_num`	TINYINT UNSIGNED NOT NULL COMMENT '선호 방 개수',
                         `prefer_area`	DECIMAL(6,3) UNSIGNED	NOT NULL COMMENT '선호 면적',
                         `is_park`	BOOLEAN	NOT NULL COMMENT '주차장 여부',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                         `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                         `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '게시글 활성 상태',
                         CONSTRAINT `fk_posts_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                         CONSTRAINT `fk_posts_houses` FOREIGN KEY (`traded_house_id`) REFERENCES `traded_houses`(`traded_house_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                         CONSTRAINT `chk_view_positive` CHECK (`view_count` >= 0)
);

CREATE TABLE `comments` (
                            `comment_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 id',
                            `parent_comment_id`	BIGINT	NULL COMMENT '부모 댓글',
                            `user_id`	BIGINT	NOT NULL COMMENT '유저 id',
                            `post_id`	BIGINT	NOT NULL COMMENT '게시글 id',
                            `traded_house_id`	BIGINT	NULL COMMENT '집 id',
                            `content`	TEXT	NOT NULL COMMENT '본문',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                            `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '댓글 활성 상태',
                            CONSTRAINT `fk_comments_parent_comments` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments`(`comment_id`) ON DELETE SET NULL ON UPDATE CASCADE,
                            CONSTRAINT `fk_comments_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT `fk_comments_posts` FOREIGN KEY (`post_id`) REFERENCES `posts`(`post_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT `fk_comments_houses` FOREIGN KEY (`traded_house_id`) REFERENCES `traded_houses`(`traded_house_id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `favorites` (
                             `favorite_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '즐겨찾기 id',
                             `user_id`	BIGINT	NOT NULL COMMENT '즐겨찾기 유저',
                             `traded_house_id`	BIGINT	NOT NULL COMMENT '즐겨 찾기 id',
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                             `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                             `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '즐겨찾기 활성상태',
                             CONSTRAINT `fk_favorites_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `fk_favorites_houses` FOREIGN KEY (`traded_house_id`) REFERENCES `traded_houses`(`traded_house_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `analysis` (
                            `analysis_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '분석 id',
                            `overallAssessment`	TEXT	NOT NULL COMMENT '종합평가',
                            `riskFactor1`	TEXT	COMMENT '위험요소1',
                            `solution1`	TEXT COMMENT '해결방안1',
                            `riskFactor2`	TEXT COMMENT '위험요소2',
                            `solution2`	TEXT COMMENT '해결방안2',
#                             `risk_degree`	TINYINT UNSIGNED NOT NULL COMMENT '위험도 0:안전 커질수록 위험',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '분석 생성시간',
                            `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '분석 수정시간',
                            `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '분석 활성 상태'
);

ALTER TABLE analysis
    CHANGE COLUMN summary overall_assessment TEXT,
    ADD COLUMN risk_factor1 TEXT AFTER overall_assessment,
    ADD COLUMN solution1 TEXT AFTER risk_factor1,
    ADD COLUMN risk_factor2 TEXT AFTER solution1,
    ADD COLUMN solution2 TEXT AFTER risk_factor2;

ALTER TABLE analysis
    DROP COLUMN risk_degree;


CREATE TABLE `contracts` (
                             `contract_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '계약서 id',
                             `analysis_id`	BIGINT	NOT NULL COMMENT '분석 결과 id',
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                             `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                             `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '계약서 활성 상태',
                             CONSTRAINT `fk_contracts_analysis` FOREIGN KEY (`analysis_id`) REFERENCES `analysis`(`analysis_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `contract_file_paths` (
                                       `contract_path_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       `file_path`	VARCHAR(255)	NOT NULL,
                                       `contract_id`	BIGINT	NOT NULL,
                                       CONSTRAINT `fk_contract_file_paths_contracts` FOREIGN KEY (`contract_id`) REFERENCES `contracts`(`contract_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `assessment_houses` (
                                     `assessment_house_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '집 id',
                                     `location` POINT NOT NULL SRID 4326 COMMENT '위치',
                                     `price` INT UNSIGNED NOT NULL COMMENT '사용자 매매가 (만원 단위)',
                                     `market_price` INT UNSIGNED NOT NULL COMMENT '근처 시세',
                                     `area` DECIMAL(6,3) UNSIGNED NOT NULL COMMENT '평수',
                                     `floor` TINYINT NOT NULL COMMENT '층수',
                                     `address` VARCHAR(255) NOT NULL COMMENT '주소',
                                     `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                                     `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                                     `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '진단서 활성 상태',
									 `is_safe` BOOLEAN NOT NULL COMMENT '깡통 판단',
                                     SPATIAL INDEX idx_location (location)
);

CREATE TABLE `registers` (
                             `register_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '등본 id',
                             `analysis_id`	BIGINT	NOT NULL COMMENT '분석 결과 id',
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                             `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                             `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '등본 활성 상태',
                             CONSTRAINT `fk_registers_analysis` FOREIGN KEY (`analysis_id`) REFERENCES `analysis`(`analysis_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `register_file_paths` ( 
                                       `register_path_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                       `file_path`	VARCHAR(255)	NOT NULL,
                                       `register_id`	BIGINT	NOT NULL,
                                       CONSTRAINT `fk_register_file_path_registers` FOREIGN KEY (`register_id`) REFERENCES `registers`(`register_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `assessments` (
                               `assessment_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '진단서 id',
                               `completeness`	TINYINT UNSIGNED NOT NULL COMMENT '진단 진행도',
                               `user_id`	BIGINT	NOT NULL COMMENT '유저 id',
                               `contract_id`	BIGINT	NOT NULL COMMENT '계약서 id',
                               `register_id`	BIGINT	NOT NULL COMMENT '등본 id',
                               `assessment_house_id`	BIGINT	NOT NULL COMMENT '진단 집 id',
                               `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
                               `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
                               `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '진단서 활성 상태',
                               CONSTRAINT `fk_assessments_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_assessments_contracts` FOREIGN KEY (`contract_id`) REFERENCES `contracts`(`contract_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_assessments_registers` FOREIGN KEY (`register_id`) REFERENCES `registers`(`register_id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `fk_assessments_assessment_houses` FOREIGN KEY (`assessment_house_id`) REFERENCES `assessment_houses`(`assessment_house_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

