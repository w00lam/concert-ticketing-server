---

# 1. Overview

콘서트 예약 시스템의 핵심 비즈니스 플로우를 시퀀스 다이어그램으로 정리합니다.

---

# 2. 콘서트 조회 (콘서트 목록 → 날짜 → 좌석)

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant DB as PostgreSQL

    User ->> API: GET /concerts
    API ->> App: Forward request
    App ->> DB: SELECT * FROM concerts WHERE deleted_at IS NULL
    DB -->> App: Concert list
    App -->> API: 200 OK
    API -->> User: Concert list

    User ->> API: GET /concerts/{concertId}/dates
    API ->> App: Forward request
    App ->> DB: SELECT * FROM concert_dates WHERE concert_id = ? AND deleted_at IS NULL
    DB -->> App: Date list
    App -->> API: 200 OK
    API -->> User: Date list

    User ->> API: GET /concert-dates/{dateId}/seats
    API ->> App: Forward request
    App ->> DB: SELECT seats + seat_status (booked/pending)
    DB -->> App: Seat list
    App -->> API: 200 OK
    API -->> User: Seat list with availability
```

---

# 3. 좌석 예약 요청 (임시 예약: TEMP_HOLD)

좌석 충돌 방지를 위해 **Redis 분산락 + 예약 테이블의 활성 예약 조건**을 함께 사용합니다.
좌석 자체나 Redis에 임시 점유 상태를 저장하지 않고, `reservations.status`와 `tempHoldExpiresAt`으로 활성 예약 여부를 판단합니다.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant Redis as Redis Cluster
    participant DB as PostgreSQL

    User ->> API: POST /reservations (seatId)
    API ->> App: Forward request

    Note over App,Redis: 1) Seat Lock 획득
    App ->> Redis: TRY_LOCK("seat:{seatId}") with TTL(3 sec)
    Redis -->> App: Lock OK

    Note over App,DB: 2) 활성 예약 조건 검사
    App ->> DB: SELECT active reservation by seatId
    DB -->> App: none

    Note over App,DB: 3) DB 예약 레코드 생성 (status=TEMP_HOLD)
    App ->> DB: INSERT INTO reservations(seat_id, user_id, status, tempHoldExpiresAt)
    DB -->> App: created

    App -->> API: 201 Created
    API -->> User: reservationId + expiresAt

    Note over App,Redis: Lock release by compare-and-delete Lua script
```

---

# 4. 결제 요청 → 승인 → 예약 확정

결제는 사용자 포인트 차감, 예약 확정, 결제 생성을 하나의 트랜잭션 흐름에서 처리합니다.
예약 확정 이벤트는 트랜잭션 커밋 이후 Kafka로 전달됩니다.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant Kafka as Kafka Producer
    participant Consumer as Reservation Consumer
    participant DB as PostgreSQL

    User ->> API: POST /payments
    API ->> App: Forward request

    App ->> DB: Load reservation and user
    App ->> DB: Deduct points
    App ->> DB: Confirm reservation
    App ->> DB: Insert payment

    Note over App: Transaction commit

    App ->> Kafka: Publish reservation confirmed event after commit
    Kafka -->> Consumer: Consume event

    App -->> API: 200 OK
    API -->> User: Payment Success + Seat Confirmed
```

---

# 5. 예약 실패 / 좌석 만료 흐름

임시 예약의 만료 여부는 `reservations.tempHoldExpiresAt`으로 판단합니다.
만료된 `TEMP_HOLD` 예약은 활성 예약 조건에서 제외되므로 새 예약을 막지 않습니다.

```mermaid
sequenceDiagram
    participant DB as PostgreSQL

    Note over DB: tempHoldExpiresAt < now

    DB -->> DB: active reservation check excludes expired TEMP_HOLD
    DB ->> DB: optional batch update status='EXPIRED'
```

---

# 6. 포인트 충전 / 조회 API

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant DB as PostgreSQL
    participant Kafka as Kafka

    User ->> API: GET /points
    API ->> App: Forward request
    App ->> DB: SELECT points WHERE user_id=?
    DB -->> App: Result
    App -->> API: 200 OK
    API -->> User: point info

    User ->> API: POST /points/charge
    API ->> App: Forward request
    App ->> DB: UPDATE points SET balance = balance + ?
    DB -->> App: OK

    App ->> Kafka: Publish "points.charged"
    Kafka -->> App: OK

    App -->> API: 200 OK
    API -->> User: charged balance
```

---

# 7. 대기열 토큰 발급

대기열은 Redis Sorted Set 사용
(score = timestamp)
TTL 자동 만료 적용.

```mermaid
sequenceDiagram
    participant User
    participant API as API Gateway
    participant App as App Server
    participant Redis as Redis Cluster

    User ->> API: POST /queue/token
    API ->> App: Forward request

    App ->> Redis: ZADD queue:concert:{id} score=timestamp member=userId
    Redis -->> App: OK

    App ->> Redis: ZRANK queue userId
    Redis -->> App: position

    App -->> API: 201 Created (token, position)
    API -->> User: token + 대기열 순번
```

---

# 8. 대기열 진입 후 실제 예약 API 호출

대기열이 0에 가까워지면 티켓 구매 API 접근 허용.

```mermaid
sequenceDiagram
    participant User
    participant API
    participant App
    participant Redis

    User ->> API: GET /queue/status
    API ->> App: Forward request

    App ->> Redis: ZRANK queue userId
    Redis -->> App: position

    App -->> API: position
    API -->> User: Wait position

    Note over User: position <= N → 예약 API 접근 가능
```

---

# 9. 예약 전체 통합 플로우 (요약 종합 버전)

```mermaid
sequenceDiagram
    participant User
    participant API
    participant App
    participant Redis
    participant DB
    participant Kafka
    participant Consumer

    User ->> API: 조회
    API ->> App: seats/dates info
    App ->> DB: fetch
    App -->> User: seat list

    User ->> API: 예약 시도(seatId)
    API ->> App: Forward
    App ->> Redis: Seat lock
    App ->> DB: Check active reservation
    App ->> DB: Create reservation(TEMP_HOLD)

    User ->> API: 결제
    App ->> DB: Deduct points + create payment
    App ->> DB: payment + reservation=CONFIRMED
    App ->> Kafka: publish reservation confirmed after commit

    App -->> User: 결제 + 예약 성공
```

---
