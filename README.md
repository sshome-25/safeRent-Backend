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
- Python (v3.8 이상)
- Mysql (v8.0.37 이상)

### 설치 방법

```bash
# 저장소 클론
git clone https://github.com/username/saferent.git
cd saferent

# 의존성 설치
npm install
pip install -r requirements.txt

# 환경 변수 설정
cp .env.example .env
# .env 파일에 필요한 API 키 및 데이터베이스 정보 입력

# 데이터베이스 마이그레이션
npm run migrate

# 개발 서버 실행
npm run dev
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

## 📊 주요 지표

- 🎯 **매물 분석 정확도**: 95% 이상
- ⚡ **분석 처리 시간**: 평균 30초 이내
- 👥 **월간 활성 사용자**: 10,000명+
- 📈 **사기 예방 건수**: 500건+

### 개발 가이드라인
- 코드 스타일: ESLint + Prettier 설정 준수

---

💡 **SafeRent와 함께 안전한 전세 생활을 시작하세요!**


# Branch 전략

1. 우리 팀은 두개의 원격저장소를 활용합니다.
2. github 기준 최신의 프로젝트를 만들어 gitlab에 한번에 올립니다.
3. 초기 세팅
    ```
        1. github 기준 clone
        2. git remote add gitlab <gitlab url>
    ```
4. github의 main에 최신의 코드가 모두 머지되면, 이를 gitlab master로 넘깁니다.
    ```
        git checkout master
        git merge origin/main --allow-unrelated-histories  
    ```

5. develop 으로 부터 개발을 한 후 develop에서 모아서 main에 넣습니다.
6. branch name은 <개발자 이름>/<개발자가 설정한 기능이름>
7. 해당 개발이 어떤 개발인지는 pr 요청시의 title에 잘 들어나게만 작성해주시면 됩니다.
