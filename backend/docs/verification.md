# 구현 검증 기록

최신 검증일: 2026-10-12. 모집글 API를 새로 구현하고 기존 Participation API와 함께 검증했다.

## 모집글 API 추가 후 검증

| 검증 | 결과 |
|---|---|
| H2 통합 테스트 | 모집글 39개 + 기존 참여 48개 = 87개 통과 |
| MariaDB 11.4.10 / InnoDB 전용 새 임시 DB | 동일 87개 통과, 실패/오류/skip 0 |
| Flyway V1 + V2 적용 / Hibernate validate | 전용 MariaDB에서 성공 |
| Maven package (선행 테스트 통과 후) | 성공, 실행 JAR 생성 |
| 실행 JAR demo 실제 HTTP | 모집글 smoke 및 기존 참여 smoke 모두 통과 |
| MySQL 8.4 Testcontainers | 미실행: Docker가 없으며 실행 경로만 제공 |

두 DB의 합계는 174회 성공이다. H2 실행 시 외부 DB 전용 테스트 87개는 환경변수 미설정으로 skip되었고, 별도 전용 MariaDB 실행에서는 해당 87개가 모두 실행·성공했다. MariaDB를 MySQL로 검증했다고 표현하지 않는다.

새 모집글 검증은 TAXI/DELIVERY 입력, 작성자 포함 인원, 정확한 +10분 경계, 분 단위/offset 시간, BR-03의 OPEN/FULL/CLOSED 포함 및 CANCELLED/FINISHED/REMOVED 제외, 작성자 권한, 참가/마감에 따른 핵심 조건 잠금, PATCH 생략/null 의미, 타입·길이·HTTPS, 정보 공개 권한, FINISHED 계산, 목록 필터/정렬/페이지, JSON 타입·중복 키·위조 필드, CSRF를 확인했다.

동시성 테스트는 최초 동시 작성, 시간 수정과 작성의 경쟁, 참여 커밋 뒤 대기 중인 수정의 조건 재검사, 작성자/POST 잠금 뒤 시간 재검사를 포함한다. 기존 참여의 마지막 자리 경쟁·중복 신청·취소/숨김 경합·DB CHECK 실패 시 롤백도 계속 통과했다.

실제 HTTP 흐름은 작성 → 비참가자 상세/링크 비노출 → 작성자 수정 → 참여 → 인원/링크 반영 → 핵심 조건 수정 409 → 중복 생성 409 → 유형 목록 → 참여 취소/링크 차단이다. 기존 참여/취소/재참여 ID 유지/명단/내역 smoke도 통과했다.

학교 SSO와 팀의 실제 USERS 테이블 통합은 기존처럼 미구현/미검증이며 팀 환경에서 연결해야 한다. PM v4에 맞춰 이 독립 프로젝트의 최소 사용자 테이블명을 users로 변경했다.

## 이전 Participation 단독 검증 기록

검증일: 2026-10-06

| 검증 | 결과 |
|---|---|
| H2 2.3.232 통합 계약 테스트 | 48개 통과, 실패/오류/skip 0 |
| MariaDB 11.4.10 / InnoDB 전용 임시 DB | 동일 48개 통과, 실패/오류/skip 0 |
| Flyway V1 적용 + Hibernate validate | MariaDB 검증 DB에서 성공 |
| Maven package | 성공, 실행 가능한 Spring Boot JAR 생성 |
| JAR demo 실행 + 실제 HTTP smoke script | 성공 |
| 실제 MySQL 8.4 Testcontainers | 미실행: 현재 환경에 Docker 없음 |

동일한 계약 테스트를 두 DB에서 실행한 총 96회 결과입니다. MySQL에서 실행했다는 의미는 아닙니다. MySQL 검증 경로는 `mvnw.cmd -Pmysql-tests verify`로 제공했습니다.

주요 검증은 작성자 포함 인원, 재참여 ID/최초 시각 유지, 정확한 T-10 경계, 잠금 대기 후 시각 재검사, 마지막 한 자리의 8개 동시 신청, 같은 사용자의 동시 중복 신청, 참여와 모집 취소/관리자 삭제의 경합, DB CHECK 오류 시 참여 생성과 FULL 전환의 동시 롤백입니다.

HTTP 테스트는 201/200, Location, 시간/ID 표현, 잘못된 ID/쿼리, 요청 사용자 위조 차단, 인증 및 CSRF, 명단 권한, 삭제된 모집의 최소 내역, 잘못된 메서드 405와 존재하지 않는 경로 404를 확인합니다. 실제 서버 smoke 검증은 Basic 인증, 쿠키/CSRF 토큰, 참여·취소·재참여·본인 상태·명단·내역 흐름을 확인했습니다.

학교 SSO, 운영 CORS/세션 정책, 기존 팀 USER 테이블과의 통합은 아직 검증 대상이 아닙니다. 해당 모듈 연결 후 팀 환경에서 추가 통합 검증이 필요합니다.
