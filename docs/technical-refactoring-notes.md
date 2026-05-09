# Refactoring Notes

이 문서는 콘서트 티켓팅 서버의 주요 리팩토링 의사결정과 검증 포인트를 정리한 기록입니다.
단순 코드 정리보다 트래픽, 동시성, 이벤트 후속 처리, 테스트 신뢰성 관점에서 의미 있는 변경을 중심으로 기록합니다.

## 1. 결제 이후 이벤트 후속 처리

### Before

- 결제 완료 후 예약 확정, 랭킹 반영, 외부 데이터 플랫폼 전달이 하나의 흐름처럼 보였습니다.
- 이벤트 테스트의 이름과 주석이 깨져 있어 의도를 파악하기 어려웠습니다.
- Kafka 통합 테스트는 Consumer 호출 여부만 확인해 이벤트 payload가 올바른지 드러나지 않았습니다.

### After

- 예약 확정 이벤트는 `@TransactionalEventListener(phase = AFTER_COMMIT)` 이후 Kafka로 전달됩니다.
- Kafka 통합 테스트는 예약 ID와 콘서트 ID가 포함된 `ReservationConfirmedEvent`가 Consumer까지 도달하는지 검증합니다.
- Producer, Consumer, Listener의 역할 설명을 간결하게 정리했습니다.

### Technical Points

- 트랜잭션 커밋 이후 이벤트 발행으로 롤백 데이터 전파를 방지했습니다.
- Kafka Producer/Consumer를 포트와 어댑터로 분리해 외부 메시징 의존성을 격리했습니다.
- Embedded Kafka 기반 통합 테스트로 비동기 이벤트 전달을 검증했습니다.

## 2. Redis 기반 대기열

### Before

- Redis Sorted Set 기반 대기열 테스트의 이름과 주석이 깨져 있었습니다.
- 테스트가 어떤 순서 보장과 dequeue 동작을 검증하는지 한눈에 보기 어려웠습니다.

### After

- 대기열 통합 테스트를 "입력 순서대로 순번 부여 -> 첫 사용자 제거 -> 순번 재계산" 흐름으로 정리했습니다.
- 첫 사용자가 제거된 뒤 rank가 `null`이 되고, 다음 사용자가 1순위가 되는 것까지 검증합니다.

### Technical Points

- Redis Sorted Set을 사용해 가입 시각 기준 대기열 순서를 관리했습니다.
- `ZPOPMIN` 기반 dequeue로 첫 대기자 조회와 제거를 원자적으로 처리합니다.
- 통합 테스트로 Redis 자료구조 선택의 동작 근거를 보여줄 수 있습니다.

## 3. 통합 테스트 Fixture 정리

### Before

- 통합 테스트마다 사용자, 콘서트, 좌석, 예약, 결제 준비 코드가 반복됐습니다.
- 테스트 본문에 fixture 생성 세부사항이 섞여 검증 의도가 흐려졌습니다.

### After

- `ReservationIntegrationTestBase`에 `createReservedSeat`, `createReservedSeatId`, `cardPaymentCommand` 같은 helper를 추가했습니다.
- 결제 멱등성, 결제 동시성, 랭킹, 포인트 동시성 테스트가 비즈니스 시나리오 중심으로 읽히도록 정리했습니다.

### Technical Points

- 테스트 데이터 준비와 검증 목적을 분리해 테스트 가독성을 개선했습니다.
- 동시성/이벤트 통합 테스트의 반복 코드를 줄여 유지보수성을 높였습니다.
- 테스트 helper가 도메인 시나리오 용어를 사용하도록 만들어 문서성과 재사용성을 높였습니다.

## 4. 설명 가능한 리팩토링 키워드

- Hexagonal Architecture: UseCase, Port, Adapter 경계를 통해 외부 인프라 의존성을 격리했습니다.
- Transactional Event: 커밋 이후 후속 처리를 분리해 데이터 정합성을 지켰습니다.
- Kafka Integration Test: Embedded Kafka와 Awaitility로 비동기 메시지 전달을 검증했습니다.
- Redis Sorted Set: 대기열 순번과 원자적 dequeue를 Redis 자료구조로 해결했습니다.
- Concurrency Test Support: 반복되는 CountDownLatch/ExecutorService 패턴을 공통 유틸로 추출했습니다.
- Read Projection: 조회 API는 엔티티 로딩 대신 Result projection을 반환하도록 정리했습니다.
