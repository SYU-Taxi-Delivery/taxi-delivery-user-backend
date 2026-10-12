# 캡스톤 모집글 / Participation API

기능정의서 v1.1 및 PM의 ERD v4 규칙을 적용한 Spring Boot 프로젝트입니다. Java 17 이상, Spring Data JPA, Spring Security, Flyway, MySQL을 사용합니다.

| 모집글 기능 | API |
|---|---|
| 작성 | POST /api/posts |
| 수정 | PATCH /api/posts/{postId} |
| 상세 | GET /api/posts/{postId} |
| 목록 / 유형·상태 필터 | GET /api/posts |

기존 Participation API는 유지합니다. 모든 사용자 기능은 인증이 필요하며 학교 로그인은 팀 인증 모듈의 CampusPrincipal 어댑터로 연결합니다.

JDK 17 이상과 JAVA_HOME을 설정하고 `backend`에서 실행합니다.

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=demo'
```

demo는 DB 설치 없이 H2로 localhost:8080에서 실행합니다. author/student1~3/admin의 공통 비밀번호는 demo-pass이며 운영 프로필에서는 테스트 계정/Basic 로그인을 제공하지 않습니다.

- 실행·인증 연동: [backend/README.md](backend/README.md)
- 모집글 API 명세: [backend/docs/post-api.md](backend/docs/post-api.md)
- 모집글 비즈니스 규칙: [backend/docs/post-business-rules.md](backend/docs/post-business-rules.md)
- 모집글 호출 예제: [backend/requests/post.http](backend/requests/post.http)
- 모집글 HTTP 검증: [backend/scripts/smoke-post-demo.ps1](backend/scripts/smoke-post-demo.ps1)
- 요청·응답·오류 명세: [backend/docs/participation-api.md](backend/docs/participation-api.md)
- IntelliJ 호출 예제: [backend/requests/participation.http](backend/requests/participation.http)
- 실제 HTTP 검증: [backend/scripts/smoke-demo.ps1](backend/scripts/smoke-demo.ps1)

구현은 `backend/`에 있습니다. 참여 신청, 참여 취소, 재참여, 본인 상태, 참가자 명단, 본인 참여 내역을 제공합니다. 학교 로그인은 팀의 인증 모듈과 연결할 수 있는 인터페이스를 제공하고, 로컬 확인에는 별도의 `demo` 프로필을 사용합니다.

모집글 모듈은 `backend/src/main/java/kr/ac/syu/capstone/post/`입니다. 소스·DB 마이그레이션·테스트·명세·Maven Wrapper를 Git에 포함하고 `work/`, 빌드 결과, 실제 비밀번호/환경 파일은 제외합니다. 백업과 전용 테스트 DB도 `work/` 안에 보관합니다.
