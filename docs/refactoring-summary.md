# Refactoring Summary

이 문서는 콘서트 티켓팅 서버를 포트폴리오 관점에서 설명하기 위한 리팩토링 요약이다. 단순히 파일을 옮긴 기록이 아니라, 대용량 트래픽과 동시성 문제가 있는 도메인에서 어떤 기준으로 구조와 API 계약을 개선했는지 정리한다.

## Goals

- 기능별 경계를 명확히 드러내는 패키지 구조 만들기
- 좌석 예약, 결제, 대기열 흐름의 일관성 리스크 줄이기
- API 성공/실패 응답을 클라이언트가 예측 가능한 형태로 표준화하기
- 요청 검증과 예외 처리를 컨트롤러 밖으로 정리하기
- 테스트 실행을 로컬 단위 테스트와 인프라 의존 테스트로 분리하기
- Swagger UI와 README 문서로 프로젝트 이해 비용 낮추기

## 1. Feature-Based Package Structure

### Before

기존 구조는 `application`, `domain`, `presentation`, `infrastructure`가 루트에 있고 그 아래에 기능이 나뉘어 있었다. 계층 중심 구조라 작은 프로젝트에서는 단순하지만, 기능이 늘어날수록 한 기능을 이해하기 위해 여러 루트 패키지를 오가야 했다.

### After

기능을 루트 패키지로 올리고, 각 기능 아래에 계층을 둔다.

```text
kr.hhplus.be.server
├── concert
├── reservation
├── payment
├── point
├── tokenqueue
├── user
├── common
├── application.event
└── infrastructure
```

각 기능은 대체로 다음 구조를 가진다.

```text
feature
├── application
├── domain
├── infrastructure
└── presentation
```

### Effect

- 기능 단위 탐색이 쉬워졌다.
- 애플리케이션 계층, 도메인 계층, 인프라 어댑터의 역할이 기능별로 모였다.
- JPA repository도 기능별 `infrastructure.persistence`로 이동해 소유권이 명확해졌다.

## 2. Reservation Consistency Improvements

대용량 티켓팅 서비스에서 가장 중요한 문제는 좌석 중복 예약과 예약 상태 불일치다.

개선한 내용:

- 활성 예약 상태 기준으로 좌석 중복 예약을 검사한다.
- 좌석 잠금 해제는 Redis Lua script 기반 compare-delete로 처리한다.
- 대기열 dequeue는 Redis sorted set의 원자적 pop 연산을 사용한다.
- 예약 확정/취소 이벤트는 트랜잭션 커밋 이후 처리되도록 정리했다.
- 결제 생성 흐름에 트랜잭션 경계를 명시했다.

### Effect

- 동일 좌석에 대한 중복 처리 가능성을 줄였다.
- 락 소유자가 아닌 요청이 락을 해제하는 문제를 방지했다.
- 이벤트 후처리가 롤백된 트랜잭션을 기준으로 실행되는 위험을 줄였다.

## 3. Test Task Separation

기본 `test` task는 Docker 없이 실행할 수 있도록 구성했다.

```bash
./gradlew test
```

인프라가 필요한 테스트는 별도 task로 분리했다.

```bash
./gradlew integrationTest
```

### Effect

- 로컬 개발 중 빠르게 단위 테스트를 실행할 수 있다.
- MySQL, Redis, Kafka, Testcontainers가 필요한 테스트를 명확히 분리했다.
- CI에서도 기본 검증과 인프라 검증을 단계별로 구성하기 쉬워졌다.

## 4. Common API Response

성공 응답은 `ApiResponse<T>`로 통일했다.

```json
{
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

에러 응답은 `ApiErrorResponse`로 통일했다.

```json
{
  "status": 400,
  "message": "요청 본문은 필수입니다.",
  "code": "REQUEST_BODY_REQUIRED",
  "data": null
}
```

### Effect

- 성공/실패 응답 모두 `status`, `message`, `data`를 포함한다.
- 실패 응답은 `code`를 추가로 제공해 클라이언트가 메시지 문자열을 파싱하지 않아도 된다.
- API 응답 계약을 문서화하기 쉬워졌다.

관련 문서:

- [API Response Policy](api-response-policy.md)

## 5. Exception Classification

예외를 다음 세 가지 주요 범주로 나눴다.

| Exception | HTTP Status | Purpose |
| --- | --- | --- |
| `ClientInputException` | `400` | 요청 값 오류 |
| `ResourceNotFoundException` | `404` | 리소스 조회 실패 |
| `BusinessRuleViolationException` | `409` | 도메인 상태 충돌 |

Spring MVC 예외도 글로벌 핸들러에서 처리한다.

- `MethodArgumentNotValidException`
- `HttpMessageNotReadableException`
- `MethodArgumentTypeMismatchException`
- `NoResourceFoundException`
- fallback `Exception`

### Effect

- 도메인 규칙 위반과 단순 요청 형식 오류를 구분할 수 있다.
- 예상하지 못한 예외의 내부 메시지를 외부에 노출하지 않는다.
- 한글 메시지와 안정적인 에러 코드를 함께 제공한다.

## 6. Bean Validation

컨트롤러의 수동 검증을 DTO 기반 Bean Validation으로 이동했다.

예시:

```java
public record PaymentRequest(
        @NotNull UUID reservationId,
        @Positive int amount,
        @NotNull PaymentMethod method
) {
}
```

컨트롤러는 `@Valid @RequestBody`만 사용한다.

### Effect

- 컨트롤러가 유스케이스 호출과 응답 변환에 집중한다.
- 요청 검증 규칙이 DTO 필드와 함께 위치한다.
- 글로벌 예외 핸들러에서 검증 실패 응답을 일괄 처리한다.

## 7. Swagger and OpenAPI

`springdoc-openapi`를 추가해 Swagger UI를 제공한다.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

컨트롤러와 DTO에 OpenAPI annotation을 추가했다.

- `@Tag`
- `@Operation`
- `@Parameter`
- `@Schema`

### Effect

- API 탐색과 테스트가 쉬워졌다.
- 포트폴리오에서 API 표면을 직접 보여줄 수 있다.
- 공통 응답, 요청 DTO, 에러 응답의 의미가 문서에 드러난다.

## 8. README Rewrite

깨져 보이던 기존 README를 프로젝트 소개 문서로 재작성했다.

포함한 내용:

- 프로젝트 목적
- 핵심 기능
- 기술 스택
- 패키지 구조
- API 응답 정책
- 예외 처리
- Bean Validation
- 동시성/일관성 포인트
- 실행 및 테스트 방법
- 문서 링크

### Effect

- 처음 프로젝트를 보는 사람이 핵심 문제와 해결 전략을 빠르게 파악할 수 있다.
- 포트폴리오에서 설명할 수 있는 개선 포인트가 README에 정리되었다.

## Commit Highlights

주요 변경은 작은 단위의 커밋으로 나누었다.

| Area | Example Commit |
| --- | --- |
| Package structure | `refactor: reorganize feature package structure` |
| User package cleanup | `refactor: move user package into feature structure` |
| API validation | `refactor: move request validation to bean validation` |
| Error handling | `refactor: separate application exceptions` |
| Error codes | `refactor: specialize error codes and messages` |
| API response | `refactor: wrap successful api responses`, `refactor: unify error api responses` |
| Swagger | `feat: add swagger api docs`, `feat: annotate openapi controllers and dto` |
| Documentation | `docs: document api response policy`, `docs: update project readme` |

## Next Candidates

아직 더 개선할 수 있는 영역도 남아 있다.

- 도메인별 세부 예외 클래스 추가
- OpenAPI response example 보강
- 성능 테스트 결과와 병목 분석 문서 최신화
- CI에서 `test`와 `integrationTest`를 분리 실행
- Docker Compose 기반 로컬 실행 환경 정리
