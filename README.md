# SYU 택시·배달 사용자 백엔드

삼육대학교 재학생을 위한 교내 택시·배달 카풀 및 합승 웹 서비스의 사용자/인증 백엔드 모듈입니다.  
학교 공식 이메일(`@syuin.ac.kr`) 인증을 통한 학생 신분 검증, ERD 기반 3단 계층 회원 관리(USERS, USER_AUTH, LOCAL_CREDENTIAL), 그리고 SSO 불가 시를 대비한 자체 로그인(Fallback) 및 계정 잠금 시스템을 제공합니다.

---

## 🛠 기술 구성

* **Java 17 / Spring Boot 3.x**
* **Spring Data JPA / Hibernate**
* **Spring Security / BCrypt Password Encoder**
* **JavaMailSender (SMTP 이메일 인증)**
* **JUnit5 / Mockito / AssertJ (단위 및 통합 테스트)**
* **Jakarta Validation**

---

## 📁 주요 패키지 및 파일 구조

* `com.school.party.domain.user.controller`
  * `SchoolEmailAuthController.java` - 이메일 인증, 회원가입, 자체 로그인 REST API 엔드포인트
* `com.school.party.domain.user.dto`
  * `SendMailCodeRequest.java` - 이메일 인증번호 발송 요청 DTO
  * `VerifyMailCodeRequest.java` - 인증번호 검증 요청 DTO
  * `SchoolRegisterRequest.java` - 회원가입 요청 DTO
  * `LocalLoginRequest.java` - 자체 로그인(Fallback) 요청 DTO
* `com.school.party.domain.user.entity`
  * `User.java` - USERS 마스터 유저 엔티티 (학번, 이메일, 권한 등)
  * `UserAuth.java` - USER_AUTH 인증 연동 엔티티 (SCHOOL_SSO 식별자 관리)
  * `LocalCredential.java` - LOCAL_CREDENTIAL 비밀번호 및 계정 잠금 관리 엔티티
  * `RefreshToken.java` - 향후 토큰 재발급용 리프레시 토큰 엔티티
* `com.school.party.domain.user.enums`
  * `Role.java` - 권한 구분 (`STUDENT`, `ADMIN`)
  * `AuthProvider.java` - 인증 수단 (`SCHOOL_SSO`, `LOCAL`)
  * `UserStatus.java` - 계정 상태 (`ACTIVE`, `INACTIVE`, `BLOCKED`)
* `com.school.party.domain.user.repository`
  * `UserRepository.java` - 유저 조회 및 학번/이메일 중복 검사 JPA
  * `UserAuthRepository.java` - 인증 수단 연동 JPA
  * `LocalCredentialRepository.java` - 비밀번호 및 잠금 정보 조회 JPA
* `com.school.party.domain.user.service`
  * `SchoolEmailAuthService.java` - 학교 이메일 인증, 난수 생성 및 3개 테이블 동시 가입 로직
  * `LocalAuthService.java` - 자체 로그인(Fallback) 인증 및 5회 실패 계정 잠금 로직

---

## 🔑 주요 기능 설명

* **학교 이메일 검증** (`SchoolEmailAuthService`)
  * `@syuin.ac.kr` 공식 학교 이메일 도메인 유효성을 검증하고 외부 메일을 차단합니다.

* **인증번호 발송 및 검증** (`SchoolEmailAuthService`)
  * 6자리 난수 코드를 생성하여 메일로 발송하고, 인메모리(`ConcurrentHashMap`)에서 검증합니다.

* **회원가입 및 테이블 연동** (`SchoolEmailAuthService`)
  * 이메일 인증 완료 후 `USERS`, `USER_AUTH`, `LOCAL_CREDENTIAL` 3개 엔티티를 동시에 저장합니다.

* **자체 로그인 (Fallback)** (`LocalAuthService`)
  * SSO 미지원 시 학번 또는 학교 이메일과 BCrypt 비밀번호 대조를 통해 로컬 인증을 수행합니다.

* **계정 잠금 보호 기능** (`LocalCredential` / `LocalAuthService`)
  * 비밀번호 5회 연속 오입력 시 30분간 계정을 자동 잠금(`lockedUntil`) 처리합니다.

* **사용자 권한 및 상태 관리** (`User` / `Role` / `UserStatus`)
  * `STUDENT`(학생) 및 `ADMIN`(관리자) 역할 구분과 `ACTIVE` 계정 상태를 관리합니다.

---

## 🧪 테스트 파일별 역할

### 1. `AuthAndUserTest.java`
* `TC-AUTH-01 & 02`: 이메일 인증번호 발송 및 불일치 예외 테스트
* `TC-AUTH-03`: `@gmail.com` 등 허용되지 않은 이메일 도메인 입력 차단 테스트
* `TC-USER-01 & 02`: 이메일 미인증 상태에서의 회원가입 요청 강제 차단 테스트
* `TC-USER-03`: 인증 성공 후 정상 회원가입 및 학번 중복 차단 테스트
* `TC-ROLE-01`: STUDENT / ADMIN 권한 매핑 검증

### 2. `LocalAuthTest.java`
* `TC-AUTH-FALLBACK-01`: 올바른 학번/이메일 및 비밀번호 입력 시 로그인 성공 테스트
* `TC-AUTH-FALLBACK-02`: 비밀번호 불일치 시 실패 횟수(`failed_login_count`) 증가 및 예외 테스트
* `TC-AUTH-FALLBACK-03`: 비밀번호 5회 실패로 잠긴 계정의 로그인 시도 시 30분간 접근 차단 테스트

### 3. `TaxiDeliverySharingApplicationTests.java`
* Spring Boot Context가 오류 없이 올바르게 로드되는지 확인하는 통합 테스트

---

## 🚀 시작하기

인텔리제이 터미널에서 아래 명령어로 업로드합니다:

```bash
git add .
git commit -m "docs: README.md 가독성 개선 및 상세 요약 작성"
git push -u origin main --force


## 📁 주요 패키지 및 파일 구조

```text
src/
├── main/java/
│   └── com/school/party/domain/user/
│       ├── controller/
│       │   └── SchoolEmailAuthController.java  # 이메일 인증, 회원가입, 자체 로그인 REST API 엔드포인트
│       ├── dto/
│       │   ├── SendMailCodeRequest.java        # 이메일 인증번호 발송 요청 DTO
│       │   ├── VerifyMailCodeRequest.java      # 인증번호 검증 요청 DTO
│       │   ├── SchoolRegisterRequest.java      # 회원가입 요청 DTO
│       │   └── LocalLoginRequest.java          # 자체 로그인(Fallback) 요청 DTO
│       ├── entity/
│       │   ├── User.java                       # USERS 마스터 유저 엔티티 (학번, 이메일, 권한 등)
│       │   ├── UserAuth.java                   # USER_AUTH 인증 연동 엔티티 (SCHOOL_SSO 식별자 관리)
│       │   ├── LocalCredential.java            # LOCAL_CREDENTIAL 비밀번호 및 계정 잠금 관리 엔티티
│       │   └── RefreshToken.java               # 향후 토큰 재발급용 리프레시 토큰 엔티티
│       ├── enums/
│       │   ├── Role.java                       # 권한 구분 (STUDENT, ADMIN)
│       │   ├── AuthProvider.java               # 인증 수단 (SCHOOL_SSO, LOCAL)
│       │   └── UserStatus.java                 # 계정 상태 (ACTIVE, INACTIVE, BLOCKED)
│       ├── repository/
│       │   ├── UserRepository.java             # 유저 조회 및 학번/이메일 중복 검사 JPA
│       │   ├── UserAuthRepository.java         # 인증 수단 연동 JPA
│       │   └── LocalCredentialRepository.java  # 비밀번호 및 잠금 정보 조회 JPA
│       └── service/
│           ├── SchoolEmailAuthService.java     # 학교 이메일 인증, 난수 생성 및 3개 테이블 동시 가입 로직
│           └── LocalAuthService.java           # 자체 로그인(Fallback) 인증 및 5회 실패 계정 잠금 로직
└── test/java/com/syu/taxidelivery/
    ├── AuthAndUserTest.java                    # 학교 이메일 인증, 도메인 차단, 회원가입 단위 테스트
    ├── LocalAuthTest.java                      # 자체 로그인, 비밀번호 검증, 30분 계정 잠금 단위 테스트
    └── TaxiDeliverySharingApplicationTests.java # Spring Context 로딩 검증 통합 테스트
