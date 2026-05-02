# Concert Reservation Platform

대용량 트래픽 환경을 가정한 콘서트 티켓팅 서버입니다. 좌석 선점, 예약, 결제, 포인트, 대기열, 이벤트 후처리 흐름을 중심으로 동시성 제어와 API 응답 표준화를 다룹니다.

## Why This Project

티켓팅 서비스는 짧은 시간에 많은 사용자가 같은 좌석과 같은 결제 흐름에 접근합니다. 이 프로젝트는 다음 문제를 안정적으로 처리하는 것을 목표로 합니다.

- 좌석 중복 예약 방지
- 임시 좌석 선점과 만료 처리
- 예약 확정과 결제 흐름의 일관성 유지
- 대기열 기반 접속 제어
- 예약 이벤트 기반 후처리
- 성공/실패 API 응답 표준화
- 테스트 실행 환경과 인프라 의존 테스트 분리

## Tech Stack

- Java 17
- Spring Boot 3.4
- Spring Data JPA
- MySQL
- Redis
- Kafka
- Gradle Kotlin DSL
- JUnit 5, Mockito, Testcontainers

## Core Features

### Reservation

- 콘서트 날짜별 좌석 조회
- 좌석 임시 선점
- 예약 확정
- 예약 취소
- 만료된 좌석 선점 해제

### Payment and Point

- 포인트 충전
- 포인트 조회
- 결제 생성
- 결제 성공 이벤트 발행

### Queue

- Redis Sorted Set 기반 대기열
- 사용자 입장 요청 등록
- 대기 순번 조회
- 다음 입장 사용자 조회

### Event Processing

- 예약 확정/취소 이벤트 발행
- Kafka 기반 외부 후처리 연동
- 콘서트 예약 랭킹 집계

## Package Structure

기능 중심 패키지 구조를 사용합니다.

```text
kr.hhplus.be.server
├── common
│   ├── exception
│   └── presentation
├── concert
│   ├── application
│   ├── domain
│   ├── infrastructure
│   └── presentation
├── payment
├── point
├── reservation
├── tokenqueue
├── user
├── application.event
└── infrastructure
```

각 기능 패키지는 대체로 다음 계층을 가집니다.

- `application`: use case, port
- `domain`: entity, domain service, policy
- `infrastructure`: persistence adapter, external adapter
- `presentation`: controller, request/response DTO

공통 예외, 공통 응답, 글로벌 예외 처리는 `common`에 둡니다.

## API Response Policy

성공 응답은 `status`, `message`, `data` 형식으로 통일합니다.

```json
{
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

에러 응답은 `status`, `message`, `code`, `data` 형식으로 통일합니다.

```json
{
  "status": 400,
  "message": "요청 본문은 필수입니다.",
  "code": "REQUEST_BODY_REQUIRED",
  "data": null
}
```

자세한 정책은 [API Response Policy](docs/api-response-policy.md)를 참고하세요.

## Error Handling

예외는 크게 세 범주로 나눕니다.

| Exception | HTTP Status | Purpose |
| --- | --- | --- |
| `ClientInputException` | `400` | 잘못된 요청 값 |
| `ResourceNotFoundException` | `404` | 리소스 조회 실패 |
| `BusinessRuleViolationException` | `409` | 도메인 상태상 처리 불가 |

Spring MVC 레벨 예외도 글로벌 핸들러에서 공통 에러 응답으로 변환합니다.

- JSON 파싱 실패
- Bean Validation 실패
- PathVariable 또는 query parameter 타입 불일치
- 예상하지 못한 서버 오류

## Validation

요청 DTO는 Bean Validation으로 검증합니다.

- `@NotNull`
- `@NotBlank`
- `@Positive`
- `@Valid @RequestBody`

컨트롤러는 수동 검증 대신 유스케이스 호출과 응답 변환에 집중합니다.

## Concurrency and Consistency

이 프로젝트에서 중요하게 다룬 일관성 포인트입니다.

- 좌석 예약 중복 방지를 위한 활성 예약 상태 검증
- Redis 분산 락의 안전한 해제
- 대기열 pop 처리의 원자성
- 예약 확정 이벤트의 트랜잭션 이후 발행
- 결제 흐름의 트랜잭션 경계 정리
- 기본 단위 테스트와 Docker/Testcontainers 기반 통합 테스트 분리

## Running Tests

기본 테스트는 Docker 없이 실행되도록 구성했습니다.

```bash
./gradlew test
```

인프라 의존 테스트는 별도 task로 실행합니다.

```bash
./gradlew integrationTest
```

`integrationTest`는 Docker/Testcontainers 환경이 필요합니다.

## Local Run

로컬 실행용 MySQL, Redis, Kafka는 Docker Compose로 먼저 실행합니다.

```bash
docker compose up -d
```

애플리케이션은 IDE 또는 Gradle로 실행합니다.

```bash
./gradlew bootRun
```

기본 프로필은 `local`이며 `localhost:3306`, `localhost:6379`, `localhost:9092`를 사용합니다. 자세한 실행 절차는 [Local Development Guide](docs/local-dev-guide.md)를 참고하세요.

## Environment Profiles

환경별 설정은 프로필 파일로 분리합니다.

| Profile | File | Purpose |
| --- | --- | --- |
| default | `application.yml` | 공통 애플리케이션, datasource, JPA 기본값 |
| local | `application-local.yml` | Docker Compose 기반 로컬 인프라 연결 |
| test | `src/test/resources/application-test.yml` | 테스트용 MySQL, Redis, Embedded Kafka 연결 |
| prod | `application-prod.yml` | 운영 환경변수 기반 인프라 연결 |

운영 실행 시에는 `SPRING_PROFILES_ACTIVE=prod`와 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, `KAFKA_BOOTSTRAP_SERVERS` 값을 주입합니다.

## API Docs

로컬 실행 후 Swagger UI와 OpenAPI JSON을 확인할 수 있습니다.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Documentation

- [Local Development Guide](docs/local-dev-guide.md)
- [API Response Policy](docs/api-response-policy.md)
- [Refactoring Summary](docs/refactoring-summary.md)
- [OpenAPI Spec](docs/openapi.yml)
- [ERD](docs/erd.md)
- [Infrastructure](docs/infra.md)
- [Reservation Scenario](docs/ssd.md)
- [Performance Analysis](docs/performance-analysis.md)
- [Load Test Plan](docs/load-test-plan.md)
- [Kafka Notes](docs/Kafka.md)
- [Bottleneck Analysis and Incident Response Manual](docs/bottleneck-analysis-and-incident-response-manual.md)

## Refactoring Highlights

- 기능 중심 패키지 구조로 재정리
- 통합 테스트 패키지 오타 수정
- 성공/에러 API 응답 표준화
- 세분화된 에러 코드와 한글 메시지 적용
- 글로벌 예외 핸들러 추가
- 요청 검증을 Bean Validation으로 이동
- 테스트 task를 기본 테스트와 통합 테스트로 분리
- 좌석 예약, 대기열, 분산 락, 이벤트 발행 흐름의 일관성 개선
