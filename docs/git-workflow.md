# 모집글 API Git 제출 규칙

작성일: 2026-10-12. 이번 모집글 API 제출에 적용하는 규칙이다.

## 저장소 / 브랜치

- 원격 저장소: https://github.com/SYU-Taxi-Delivery/taxi-delivery-user-backend.git
- PR 대상: main
- 작업 브랜치: codex/post-api
- 현재 저장소는 이번 제출 전 파일 없는 초기 main만 있었다. 기존 팀 프론트엔드 README의 feat/#이슈번호 형식은 참고하되, 이번 제출은 이슈 연결 없이 codex/post-api를 사용한다.
- main에 직접 기능 코드를 푸시하지 않고 작업 브랜치에서 PR을 생성한다.
- 이후 백엔드의 별도 팀 브랜치 정책이 정해지면 그 규칙을 따른다.

## 커밋 / PR

일반 커밋은 `feat: 모집글 작성·수정·상세·목록 API 구현`처럼 소문자 작업 유형과 설명을 사용한다. PR 제목은 `[FEAT] 모집글 작성·수정·상세·목록 API 구현` 형식이다.

PR은 `.github/pull_request_template.md`의 변경 내용, 관련 이슈, 검증, 동작 증거, 환경변수·의존성·주의사항을 작성한다. 조직 프론트엔드의 템플릿 구조를 참고하고 체크 항목은 Maven/백엔드 검증으로 바꿨다. 실제 실행한 검증만 체크한다.

참고 자료:

- https://github.com/SYU-Taxi-Delivery/taxi-delivery-sharing-fe#readme
- https://github.com/SYU-Taxi-Delivery/taxi-delivery-sharing-fe/blob/main/.github/pull_request_template.md

## 제출 범위

모집글 및 기존 Participation 소스, DB 마이그레이션, 테스트, API 명세, 호출 예제, 실행 안내를 포함한다. 이미지 산출물/렌더 도구, ZIP, work 백업·임시 DB·Maven 캐시, target 빌드 결과, 실제 비밀번호는 이번 PR에 포함하지 않는다.

원격 main의 초기 커밋을 기반으로 로컬 코드를 연결한다. PR 생성이 끝나도 병합은 별도 리뷰 절차에서 수행한다. 이 작업에서 main 병합이나 배포는 수행하지 않는다.

검증 상세는 [backend/docs/verification.md](../backend/docs/verification.md), API 계약은 [backend/docs/post-api.md](../backend/docs/post-api.md)를 참고한다.
