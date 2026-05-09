# Release Readiness Check

`v1.0.1` 릴리즈 기준으로 확인한 항목입니다.

## Release

| Item | Value |
| --- | --- |
| Tag | `v1.0.1` |
| Base branch | `main` |
| Previous tag | `v1.0.0` |
| Release scope | Documentation polish, boilerplate cleanup, test fixture readability |

## Checks

| Check | Result |
| --- | --- |
| 기본 테스트 | Passed |
| 통합 테스트 | Passed |
| Docker Compose 구성 검증 | Passed |
| GitHub에 올리지 않을 파일 추적 여부 | Passed |
| README 주요 링크 확인 | Passed |
| 로컬 개발 가이드 주요 링크 확인 | Passed |
| `main` 원격 동기화 | Passed |
| 릴리즈 태그 원격 push | Passed |

## Commands

```bash
./gradlew test
./gradlew integrationTest
docker compose config
git status --short --branch
git tag --list
```

## Changes Since v1.0.0

- `docs: polish refactoring documentation`
- `refactor: remove noisy boilerplate comments`
- `refactor: hide test builders behind fixtures`

## Notes

- `.env` 같은 실제 환경변수 파일은 Git에 포함하지 않습니다.
- 운영 환경변수 예시는 `.env.example`에만 문서화합니다.
- 로컬 인프라는 `docker-compose.yml`로 MySQL, Redis, Kafka만 실행하고 애플리케이션은 IDE 또는 Gradle로 실행합니다.
- GitHub Release 본문은 `v1.0.1` 태그 기준으로 작성합니다.
