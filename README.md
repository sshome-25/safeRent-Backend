# 🏠 SafeRent - 안심 전세 서비스

전세사기 피해를 예방하고 안전한 전세 계약을 지원하는 종합 플랫폼입니다.

## 📋 프로젝트 개요

SafeRent는 전세사기 피해가 증가하는 상황에서, 임차인들이 보다 안전하고 투명하게 전세 매물을 선택할 수 있도록 돕는 서비스입니다. AI 기술과 데이터 분석을 통해 매물의 안전성을 진단하고, 커뮤니티 기능을 제공하여 정보 공유를 촉진합니다.

## ✨ 주요 기능

### 🤖 1. AI 등기부등본 안전진단
- **스마트 분석**: AI가 등기부등본을 자동으로 분석하여 위험 요소를 탐지
- **위험도 평가**: 매물별 안전 평가
- **상세 리포트**: 권리관계, 담보설정, 경매정보 등 종합 분석 결과 제공

### 📍 2. 매매 데이터 기반 입지 분석
- **실거래가 시각화**: 주변 매매 데이터를 지도 위에 표시
- **입지 평가**: 교통, 편의시설, 학군 등 입지 조건 종합 평가

### 💬 3. 커뮤니티 게시판
- **정보 공유**: 전세 관련 경험과 정보 공유
- **질문 답변**: 전세 계약 관련 궁금증 해결
- **후기 게시**: 중개업소, 임대인 관련 후기 공유
- **전문가 상담**: 부동산 전문가와의 소통 창구

## 🛠 기술 스택

### Frontend
- vue.js
- JavaScript
- Kakao Map API

### Backend
- Java / Spring
- MySql

### AI/ML
- Grok API

## 🚀 설치 및 실행

### 사전 요구사항
- Java (v8 이상)
- Mysql (v8.0.37 이상)

### 설치 방법

```bash
# 저장소 클론
git clone https://lab.ssafy.com/ssafy_13th_18class/999_final/ssafy_home_final_leeheegyeong_jungyeonsu.git

# front 의존성 설치
cd front
npm install

# 환경 변수 설정
# 아래의 application.properties를 safeRent/src/main/resources/ 아래에 추가

# front 서버 실행
cd front
npm run dev

# back 서버 실행
cd safeRent

# db 세팅
# 1. docs의 schema_setup.sql 실행
# 2. docs의 traded_houses_dump.sql.gz 파일 압축 해제
mysql -u [username] -p
use safeRent;
source [traded_houses_dump.sql path];

```

### 환경 변수 설정(application.properties)

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=saferent
DB_USER=your_username
DB_PASSWORD=your_password

server.port=8080

spring.application.name=safeRent
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=
spring.servlet.multipart.max-file-size=
spring.servlet.multipart.max-request-size=

grok.api.key=

aws.s3.bucket-name=
aws.s3.region=
aws.s3.base-url=

cloud.aws.credentials.access-key=
cloud.aws.credentials.secret-key=
cloud.aws.region.static=
cloud.aws.stack.auto=

mybatis.configuration.map-underscore-to-camel-case=true
```

## 📱 사용법

### 1. 매물 안전진단
1. 등기부등본 파일 업로드
2. AI 분석 결과 확인
3. 위험 요소 및 권장사항 검토

### 2. 입지 분석
1. 관심 매물 주소 입력
2. 주변 매매 데이터 확인
3. 가격 동향 및 입지 점수 확인

### 3. 커뮤니티 참여
1. 회원가입 및 로그인
2. 게시글 작성 및 댓글 참여
3. 전문가 상담 예약

### 개발 가이드라인
- 코드 스타일: ESLint + Prettier 설정 준수

---

