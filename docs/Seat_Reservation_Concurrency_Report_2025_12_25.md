# 좌석 예약 동시성 처리 보고서

## 1. 문제 정의

콘서트 티켓팅에서는 여러 사용자가 같은 좌석을 거의 동시에 선택할 수 있습니다.
이때 하나의 좌석에 여러 임시 예약이 생성되면 결제, 예약 확정, 좌석 조회 결과가 모두 불안정해집니다.

이 프로젝트의 목표는 다음 두 가지입니다.

- 동일 좌석에 대해 동시에 예약 요청이 들어와도 활성 예약은 1건만 생성한다.
- 만료된 임시 예약은 새 예약을 막지 않는다.

## 2. 좌석 임시 예약 동시성 제어

### 설계

좌석 자체에 임시 점유 상태를 저장하지 않고, 예약 테이블의 상태와 만료 시각으로 활성 예약 여부를 판단합니다.

활성 예약 조건은 다음과 같습니다.

- `CONFIRMED`
- `TEMP_HOLD` and `tempHoldExpiresAt > now`

동일 좌석에 대한 동시 진입은 Redis 분산락으로 제어합니다.
락을 획득한 요청만 활성 예약 존재 여부를 확인하고 임시 예약을 생성합니다.

### 락 해제

락 해제는 Redis Lua script 기반 compare-and-delete 방식으로 처리합니다.
이를 통해 다른 요청이 새로 획득한 락을 이전 요청이 잘못 삭제하지 않도록 방어합니다.

### 검증

관련 테스트:

- `SeatReservationDistributedLockTest`
- `SeatTempHoldConcurrencyTest`

검증 내용:

- 여러 사용자가 동일 좌석을 동시에 예약해도 성공한 임시 예약은 1건입니다.
- 실패한 요청이 있더라도 최종 활성 예약 수는 1건으로 유지됩니다.
- 동시성 실행 패턴은 `ConcurrencyTestSupport`로 공통화했습니다.

## 3. 만료된 임시 예약 처리

### 설계

임시 예약은 `TEMP_HOLD` 상태와 `tempHoldExpiresAt` 만료 시각을 함께 가집니다.
만료 시각이 지난 임시 예약은 활성 예약 조건에서 제외됩니다.

따라서 별도 좌석 상태를 변경하지 않아도, 새 예약 요청은 만료된 임시 예약을 무시하고 다시 좌석을 선점할 수 있습니다.

### 검증

관련 테스트:

- `MakeReservationUseCaseImplTest`
- `SeatTempHoldConcurrencyTest`

검증 내용:

- 활성 임시 예약이 있으면 같은 좌석 예약을 거절합니다.
- 만료된 임시 예약은 새 임시 예약을 막지 않습니다.
- 예약 생성 책임은 `ReservationCreationService`에서 트랜잭션 단위로 처리합니다.

## 4. 포인트 차감 동시성

좌석 예약 자체와 별개로, 결제 과정에서는 사용자 포인트가 동시에 차감될 수 있습니다.
이 프로젝트는 `User` 엔티티의 `@Version`을 사용해 낙관적 락으로 잔액 정합성을 보호합니다.

관련 테스트:

- `PointDeductOptimisticLockTest`
- `ReservationPaymentConcurrencyTest`
- `PaymentConcurrencyIntegrationTest`

검증 내용:

- 같은 사용자 포인트에 동시에 차감 요청이 들어와도 최종 잔액이 깨지지 않습니다.
- 결제는 예약 단위로 1건만 생성됩니다.
- 중복 결제 요청은 멱등성 정책과 DB unique constraint로 방어합니다.

## 5. 현재 방식의 장점과 한계

### 장점

- 좌석 테이블에 임시 상태를 두지 않아 좌석 모델이 단순합니다.
- Redis 락으로 동일 좌석 동시 진입을 빠르게 제어합니다.
- 예약 테이블의 활성 조건으로 최종 중복 예약을 판단해 도메인 규칙이 명확합니다.
- 통합 테스트로 Redis, DB, 트랜잭션 경계를 함께 검증합니다.

### 한계

- Redis 장애 시 락 획득 경로가 영향을 받을 수 있습니다.
- 매우 높은 트래픽에서는 락 대기/실패 정책과 재시도 정책을 더 세밀하게 설계해야 합니다.
- 운영 환경에서는 락 TTL, 요청 타임아웃, 모니터링 지표를 함께 조정해야 합니다.

## 6. 결론

현재 구현은 Pessimistic Locking 중심이 아니라 Redis 분산락과 예약 활성 조건을 조합해 좌석 중복 예약을 방어합니다.
결제와 포인트 차감은 예약 단위 멱등성, DB unique constraint, JPA 낙관적 락으로 정합성을 보완합니다.

이 구조는 좌석 선점, 결제, 포인트 차감처럼 동시성 위험이 높은 흐름을 각각 다른 수준에서 방어하며,
통합 테스트를 통해 실제 인프라 연동 상황에서도 핵심 불변식이 유지되는지 검증합니다.

## 7. 확장 동시성 검증 결과 (2026-09-07)

기존 검증 규모를 늘려 Java 17 toolchain과 Docker/Testcontainers 환경에서 다시 실행했습니다.
좌석 예약과 동일 결제 요청은 각각 100건과 500건으로 실행했고, 대기열은 1,000명을 등록했습니다.

### 좌석 예약과 락 비교

| 검증 조건 | 락 적용 결과 | 락 제거 비교 결과 |
| --- | --- | --- |
| 동일 좌석 100건 | 활성 `TEMP_HOLD` 1건 | 활성 예약 3건 |
| 동일 좌석 500건 | 활성 `TEMP_HOLD` 1건 | 활성 예약 3건 |

락 제거 비교는 운영 코드를 변경한 것이 아니라 테스트 전용 `NoOp` 락 구현을 주입해 실행했습니다.
500건 요청에서 락 적용 시 활성 예약 1건을 유지했지만, 락을 제거하면 3건이 생성되어 정상 1건을 초과한 2건의 중복 예약이 확인됐습니다.

관련 테스트:

- `SeatReservationDistributedLockTest`
- `SeatTempHoldConcurrencyTest`
- `SeatReservationWithoutLockComparisonTest`

### 동일 결제 요청

`PaymentConcurrencyIntegrationTest`를 100건과 500건으로 실행했습니다.
두 조건 모두 결제 레코드는 1건으로 유지됐고, 시작 포인트 10,000점에서 5,000점만 차감됐습니다.

### 대기열 규모와 dequeue 경쟁

1,000명의 사용자를 Redis Sorted Set에 등록한 뒤, 한 테스트 프로세스에서 8개 동시 작업자가 `ZPOPMIN`을 호출했습니다.

- 순번 값: `1..1,000`
- dequeue 사용자: 1,000명
- 중복 dequeue: 0건
- dequeue 후 대기열 길이: 0
- 측정 처리량: 약 `4,382.99명/초`

추가로 같은 1,000명 큐를 준비한 뒤, 운영 코드의 `TokenQueueRepositoryImpl`을 사용하는 dequeue worker를 독립 JVM 2개에서 실행했습니다.
각 JVM은 별도 Lettuce Redis 연결을 사용해 500명씩 `ZPOPMIN`을 호출했습니다.

- `application_instances`: 2
- `dequeue_per_instance`: 500
- `dequeue_users`: 1,000
- `dequeue_duplicates`: 0건
- `queue_length_after_dequeue`: 0

독립 JVM 2개 결과는 Redis의 원자적 `ZPOPMIN`이 프로세스 경계를 넘어 같은 사용자를 중복으로 꺼내지 않는다는 점을 확인합니다.
이 테스트 자체는 실제 HTTP 서버를 서로 다른 포트로 띄운 end-to-end 검증이나 HTTP 처리량 측정은 아니며, 아래 k6 검증으로 보완했습니다.

### 두 HTTP 인스턴스 대상 k6 검증

기존 `POST /queue/token/dequeue` 응답 형식(`ApiResponse<Void>`)은 유지한 채, 8080·8081 두 애플리케이션 인스턴스에 HTTP 요청을 분산했습니다.
1,000명을 enqueue한 뒤 10개 VU가 총 1,000회의 dequeue를 수행했습니다.

실행 ID: `queue-20260908-164011-final`

- dequeue 요청: `1,000건`
- dequeue 처리량: `3,378.38 req/s`
- dequeue p95: `3.97ms`
- dequeue p99: `4.88ms`
- HTTP 실패율: `0%`
- 인스턴스별 dequeue: `8080 = 539건`, `8081 = 461건`
- dequeue 후 대기열 길이: `0`

처리량은 enqueue 준비 구간을 제외하고 dequeue 시나리오 구간에서 계산한 값입니다.
사용자별 중복 dequeue 여부는 `TokenQueueMultiInstanceIntegrationTest`가 사용자 ID를 수집해 별도로 검증하며, k6는 API 성능 지표를 수집합니다.
재현 절차와 Grafana 대시보드 구성은 [`docs/queue-k6-grafana-runbook.md`](queue-k6-grafana-runbook.md)에 기록했습니다.

## 8. 예약 확정 이벤트 전달 검증

`KafkaPaymentIntegrationTest`에서 결제 완료 후 예약 확정 이벤트가 Embedded Kafka를 거쳐 Consumer까지 전달되는지 확인했습니다.
예약 ID와 콘서트 ID가 일치하는 이벤트를 Consumer가 수신하는 조건을 통과했습니다.

### Consumer 실패와 DLT 이관

`KafkaErrorHandlingConfig`에 `DefaultErrorHandler`와 `DeadLetterPublishingRecoverer`를 적용했습니다.
이번 검증 기준은 1초 간격 2회 재시도, 총 3회 전달 후 `reservation-confirmed-dlt` 이관입니다.

`KafkaReservationFailureIntegrationTest`에서 외부 데이터 플랫폼 전송 실패를 강제로 발생시켰습니다.

- Consumer 전달 시도: 3회
- 재시도 횟수: 2회
- DLT 이관 레코드: 1건
- DLT 토픽: `reservation-confirmed-dlt`
- DLT 예외 메시지 헤더: 원래 실패 메시지 포함

2회라는 횟수는 이번 검증을 위한 기준이며, 외부 데이터 플랫폼의 SLA와 장애 지속 시간을 반영한 운영 정책으로 확정한 값은 아닙니다.
일반 동시성 테스트를 Kafka broker 없이 실행할 때는 `AFTER_COMMIT` 이벤트 발행이 `localhost:9092` 연결 timeout을 로그로 남겼지만,
좌석·결제 정합성 assertion은 통과했습니다. Kafka 전달과 실패 복구 결과는 Embedded Kafka를 사용하는 별도 통합 테스트에서 확인했습니다.

## 9. 로컬 Kafka Compose smoke check (2026-09-08)

로컬 Compose에서 사용하던 `bitnami/kafka:3.7` 이미지를 pull할 수 없어, 공식 Apache Kafka JVM 이미지인 `apache/kafka:3.9.1`로 교체했습니다.
공식 이미지의 기본 단일 노드 KRaft 설정을 사용하고, 로컬 검증 스택에서는 Kafka 로그를 ephemeral storage로 둬 named volume 권한 충돌을 피했습니다.

- broker image: `apache/kafka:3.9.1`
- broker healthcheck: `healthy`
- topic creation: `reservation-confirmed` 생성·조회 성공
- Kafka 전달 테스트: `KafkaPaymentIntegrationTest` 통과
- Kafka 실패·DLT 테스트: `KafkaReservationFailureIntegrationTest` 통과

Compose 수정은 [`docker-compose.yml`](../docker-compose.yml)에 반영했습니다.
