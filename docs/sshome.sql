create database if not exists sshome;
use sshome;

CREATE TABLE `roles` (
	`role_id`	INTEGER	NOT NULL PRIMARY KEY COMMENT '권한 id',
	`name`	varchar(255)	NOT NULL COMMENT '권한 이름'
);

CREATE TABLE `users` (
	`user_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '유저 id',
	`email`	varchar(255) NOT NULL COMMENT '유저 이메일',
	`password`	varchar(255) NOT NULL COMMENT '유저 비밀번호',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '유저 활성 상태',
	`nickname`	varchar(255)	NOT NULL COMMENT '유저 닉네임',
	`role_id`	INTEGER	NOT NULL COMMENT '유저 권한',
    CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles`(`role_id`) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `favorites` (
	`favorite_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '즐겨찾기 id', 
	`user_id`	BIGINT	NOT NULL COMMENT '즐겨찾기 유저',
	`house_id`	BIGINT	NOT NULL COMMENT '즐겨 찾기 id',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '즐겨찾기 활성상태',
    CONSTRAINT `fk_favorites_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_favorites_houses` FOREIGN KEY (`house_id`) REFERENCES `houses`(`house_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `posts` (
	`post_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 id',
	`user_id`	BIGINT	NOT NULL COMMENT '게시글 user_id',
	`house_id`	BIGINT	NULL COMMENT '게시글 house_id',
	`title`	varchar(255)	NOT NULL COMMENT '제목',
	`content`	text NOT NULL COMMENT '본문',
	`view_count`	INT	NOT NULL	COMMENT '조회수',
	`prefer_location`	VARCHAR(255)	NOT NULL COMMENT '선호 위치 법정동', 
	`prefer_room_num`	INT	NOT NULL COMMENT '선호 방 개수',
	`prefer_area`	INT	NOT NULL COMMENT '선호 면적',
	`parking`	BOOLEAN	NOT NULL COMMENT '주차장 여부',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '게시글 활성 상태',
    CONSTRAINT `fk_posts_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_posts_houses` FOREIGN KEY (`house_id`) REFERENCES `houses`(`house_id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `comments` (
	`comment_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '댓글 id',
	`parent_comment_id`	BIGINT	NULL COMMENT '부모 댓글',
	`user_id`	BIGINT	NOT NULL COMMENT '유저 id',
	`post_id`	BIGINT	NOT NULL COMMENT '게시글 id',
	`house_id`	BIGINT	NULL COMMENT '집 id',
	`content`	TEXT	NOT NULL COMMENT '본문',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',	
    `status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '댓글 활성 상태',
    CONSTRAINT `fk_comments_parent_comments` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments`(`comment_id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_comments_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comments_posts` FOREIGN KEY (`post_id`) REFERENCES `posts`(`post_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_comments_houses` FOREIGN KEY (`house_id`) REFERENCES `houses`(`house_id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `houses` (
	`house_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '집 id',
	`address`	VARCHAR(255)	NOT NULL COMMENT '법정동 주소',
	`location`	POINT	NOT NULL COMMENT '위치',
	`area`	DECIMAL(5,2)	NOT NULL COMMENT '평수',
	`floor`	INT	NOT NULL COMMENT '층수',
	`built_year`	INT	NOT NULL COMMENT '준공 년',
	`name`	varchar(255)	NOT NULL COMMENT '단지 이름',
	`transaction_date`	Date	NOT NULL COMMENT '거래일자',
	`price`	BigInt	NOT NULL COMMENT '가격'
);

CREATE TABLE `registers` (
	`register_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '등본 id',
	`file_path`	VARCHAR(255)	NOT NULL COMMENT '등본 파일 위치',
	`house_id`	BIGINT	NOT NULL COMMENT '집 id',
	`analysis_id`	BIGINT	NOT NULL COMMENT '분석 결과 id',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '등본 활성 상태',
    CONSTRAINT `fk_registers_houses` FOREIGN KEY (`house_id`) REFERENCES `houses`(`house_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_registers_analysis` FOREIGN KEY (`analysis_id`) REFERENCES `analysis`(`analysis_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `contracts` (
	`contract_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '계약서 id',
	`file_path`	VARCHAR(255)	NOT NULL COMMENT '파일 저장 위치',
	`house_id`	BIGINT	NOT NULL COMMENT '집 id',
	`analysis_id`	BIGINT	NOT NULL COMMENT '분석 결과 id',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '계약서 활성 상태',
    CONSTRAINT `fk_contracts_houses` FOREIGN KEY (`house_id`) REFERENCES `houses`(`house_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_contracts_analysis` FOREIGN KEY (`analysis_id`) REFERENCES `analysis`(`analysis_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `analysis` (
	`analysis_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '분석 id',
	`summary`	TEXT	NOT NULL COMMENT '분석 내용 요약',
	`warnings`	INTEGER	NOT NULL COMMENT '위험도 0:안전 커질수록 위험',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '분석 활성 상태'
);

CREATE TABLE `assessments` (
	`assessment_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '진단서 id',
	`completness`	VARCHAR(255)	NOT NULL COMMENT '진단 진행도',
	`user_id`	BIGINT	NOT NULL COMMENT '유저 id',
	`contracts_id`	BIGINT	NOT NULL COMMENT '계약서 id',
	`register_id`	BIGINT	NOT NULL COMMENT '등본 id',
	`location`	Point	NOT NULL COMMENT '위치',
	`price`	BIGINT	NOT NULL COMMENT '사용자 매매가',
	`market_price`	BIGINT	NOT NULL COMMENT '근처 시세',
	`created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '유저 생성시간',
	`updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '유저 수정시간',
	`status` ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '진단서 활성 상태',
    CONSTRAINT `fk_assessments_users` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_assessments_contracts` FOREIGN KEY (`contracts_id`) REFERENCES `contracts`(`contract_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_assessments_registers` FOREIGN KEY (`register_id`) REFERENCES `registers`(`register_id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `hospitals` (
	`hospital_id`	BIGINT	NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`location`	POINT	NOT NULL COMMENT '위치',
	`name`	varchar(255)	NOT NULL COMMENT '이름'
);

CREATE TABLE `stations` (
	`station_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`location`	POINT	NOT NULL COMMENT '위치',
	`name`	varchar(255)	NOT NULL COMMENT '이름'
);

CREATE TABLE `schools` (
	`school_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`location`	POINT	NOT NULL COMMENT '위치',
	`name`	varchar(255)	NOT NULL COMMENT '이름'
);

CREATE TABLE `stores` (
	`store_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`location`	POINT	NOT NULL COMMENT '위치',
	`name`	varchar(255)	NOT NULL COMMENT '이름'
);

CREATE TABLE `parks` (
	`park_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`location`	POINT	NOT NULL COMMENT '위치',
	`name`	varchar(255)	NOT NULL COMMENT '이름'
);

