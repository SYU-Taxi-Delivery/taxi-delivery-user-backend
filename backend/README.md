# 모집글 / Participation API 실행 안내

Java 17 이상, Spring Boot 3.5.16, Spring Data JPA, Spring Security, Flyway를 사용합니다. 운영용 스키마는 MySQL 8.0.16 이상을 대상으로 작성했습니다.

## 1. DB 설치 없이 바로 실행

PowerShell에서 `backend` 디렉터리로 이동하고 JDK의 `JAVA_HOME`을 설정합니다. Maven Wrapper가 Maven을 다운로드하므로 별도 Maven 설치는 필요 없습니다.

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=demo'
```

demo는 `127.0.0.1:8080`에 바인딩하며 H2 메모리 DB를 사용합니다. 종료하면 데이터가 사라집니다. `author`, `student1`, `student2`, `student3`, `admin` 계정을 제공하며 공통 비밀번호는 `demo-pass`입니다. HTTP Basic 인증은 이 프로필에서만 활성화됩니다.

시작 시 택시 모집 ID `1`(정원 4명, 약 2시간 뒤 출발), 배달 모집 ID `2`(정원 10명, 약 3시간 뒤 주문 마감)를 생성합니다. 작성자는 `author`입니다. 정원은 작성자를 포함합니다.

다른 PowerShell에서 실제 호출을 확인합니다.

```powershell
.\scripts\smoke-demo.ps1
```

스크립트는 student1으로 참여 → 본인 상태 → 명단 → 취소 → 재참여 → 내역 조회 → 최종 취소를 실행합니다. 반복 실행 시 기존 JOINED 기록을 먼저 취소합니다. CSRF 쿠키와 토큰을 함께 전달합니다. IntelliJ에서는 `requests/participation.http`를 열어 같은 흐름을 수동 확인할 수 있습니다.

모집글 작성·상세·수정·목록과 참여 연동은 `requests/post.http` 또는 아래 스크립트로 확인합니다.

```powershell
.\scripts\smoke-post-demo.ps1
```

명세는 [docs/post-api.md](docs/post-api.md)입니다. `src/main/java/kr/ac/syu/capstone/post/`가 모집글 모듈입니다. 기존 Participation API 경로와 응답은 유지합니다.

## 2. MySQL 실행

Docker가 설치된 환경에서는 새 DB를 다음과 같이 시작합니다. 비밀번호는 팀 환경에 맞게 설정합니다.

```powershell
$env:DB_PASSWORD = 'choose-a-local-password'
$env:MYSQL_ROOT_PASSWORD = 'choose-a-root-password'
docker compose up -d
$env:DB_USERNAME = 'capstone'
$env:DB_URL = 'jdbc:mysql://localhost:3306/capstone?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true'
.\mvnw.cmd spring-boot:run
```

Flyway가 V1과 `V2__post_api_v4.sql`을 순서대로 적용하고 Hibernate는 테이블을 검증합니다. V1이 이미 적용된 프로젝트 DB에는 V2를 추가 적용합니다. 기본 프로필은 테스트 계정이나 모집을 생성하지 않습니다. 학교 인증 어댑터를 연결하기 전에는 인증된 요청을 만들 수 없습니다.

V2 이후의 `users`는 이 독립 프로젝트에 필요한 최소 USERS 테이블입니다. 실제 팀 USERS 테이블에 합칠 때는 `AppUser` 매핑과 Flyway FK 참조를 함께 검토하고 공유 DB에 초기 V1/V2를 그대로 적용하지 않습니다. 댓글·신고·회원가입 API는 이번 구현에 포함되지 않습니다. ERD v4대로 FINISHED를 동시간 중복 검사에서 제외하고 `valid_reference_time` 생성 열은 제거합니다. POST.deleted_by는 관리자 숨김 처리자를 기록합니다.

## 3. 학교 인증 연결

인증 모듈이 학교 계정 검증 후 DB 사용자를 찾아 `CampusPrincipal`을 구현한 principal을 Spring Security의 `Authentication`에 넣도록 연결합니다. `CampusUser`가 구현 예시입니다. principal의 `userId()`는 인증 서버/DB에서 확인한 ID여야 합니다.

- 요청 Body, 쿼리, `X-User-Id` 헤더로 사용자 ID를 받지 않습니다.
- 일반 `UserDetails`나 ID 문자열만 넣으면 요청은 401로 거절됩니다.
- 운영 프로필의 인증 필터 또는 세션 로그인 기능은 팀 인증 모듈에서 추가해야 합니다. 현재 코드에 학교 SSO 또는 이메일 인증 구현은 없습니다.
- 브라우저 인증에 대비해 CSRF를 활성화했습니다. 먼저 `GET /api/csrf`를 호출한 뒤 응답의 `data.headerName`에 `data.token`을 넣고 해당 쿠키를 함께 전송합니다. JSON으로 반환된 토큰을 사용합니다.
- 프론트엔드가 별도 origin인 경우 팀의 실제 주소에 맞는 CORS/쿠키 설정을 추가해야 합니다. 현재 demo는 같은 origin 또는 HTTP 클라이언트로 확인합니다.

## 4. 모집/관리자 모듈 연결

`PostLifecycleService.close`, `cancelRecruitment`, `remove`는 다른 모듈이 호출할 내부 서비스입니다. 외부 REST 경로는 생성하지 않았습니다. 각각 작성자/관리자 권한을 DB에서 확인하고 POST 잠금을 먼저 획득합니다.

모집 수정은 같은 POST 잠금을 사용합니다. 새 모집 생성/수정은 먼저 작성자 USERS 행을 잠가 동시간 중복도 방지합니다. 참여/마감/취소 서비스는 POST 잠금을 유지하고 작성자 잠금을 요청하지 않습니다. 다른 모듈이 POST를 직접 생성/시간 변경하거나 PARTICIPATION을 직접 변경하면 이 보장을 우회합니다. 작성자는 PARTICIPATION 행을 만들지 않고 인원수 계산에 1명으로 포함합니다.

## 5. 테스트

```powershell
.\mvnw.cmd test
# Docker 사용 가능 환경: 같은 계약 테스트를 실제 MySQL 8.4에서도 실행
.\mvnw.cmd -Pmysql-tests verify
```

Docker가 없으면 MySQL Testcontainers 테스트는 skip됩니다. 기본 테스트는 H2를 사용하므로 운영 MySQL 검증과 구분해야 합니다. 공유 테스트는 실제 DB 모드에서도 동일한 정책·권한·동시 요청·롤백을 확인합니다.

외부 DB 테스트는 **매 테스트마다 users, post, participation 데이터를 삭제합니다. 반드시 폐기 가능한 전용 테스트 DB만 지정하세요.** Flyway 마이그레이션도 적용합니다.

```powershell
$env:PARTICIPATION_TEST_DB_URL = 'jdbc:mysql://127.0.0.1:3306/participation_verification?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true'
$env:PARTICIPATION_TEST_DB_USERNAME = 'test_user'
$env:PARTICIPATION_TEST_DB_PASSWORD = 'test_password'
.\mvnw.cmd '-Dtest=PostExternalDatabaseTest,ExternalDatabaseIntegrationTest' test
Remove-Item Env:PARTICIPATION_TEST_DB_URL, Env:PARTICIPATION_TEST_DB_USERNAME, Env:PARTICIPATION_TEST_DB_PASSWORD
```

DB 시간은 UTC, API 시간은 `+09:00` ISO 8601입니다. `Clock`을 주입해 정확한 마감 경계 테스트가 가능합니다. 기준시각 경과는 API에서 FINISHED로 계산하며 별도 배치의 실행 여부에 의존하지 않습니다. DB의 status를 FINISHED로 바꾸는 배치는 포함하지 않았습니다.
