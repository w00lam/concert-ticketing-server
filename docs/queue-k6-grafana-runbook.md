# 대기열 k6 + Grafana 검증 절차

이 검증은 기존 `POST /queue/token/dequeue` 응답 형식을 변경하지 않고, 두 애플리케이션 인스턴스에 HTTP 부하를 분산해 처리량과 응답 시간을 측정합니다.

사용자 중복 dequeue 여부는 API가 `Void`를 반환하므로 k6 응답만으로 판정하지 않습니다. 독립 JVM 통합 테스트인 `TokenQueueMultiInstanceIntegrationTest`가 운영 `TokenQueueRepositoryImpl`을 사용해 사용자 ID별 중복을 검증합니다.

## 1. 준비

로컬 인프라를 실행합니다.

```powershell
docker compose up -d
```

같은 MySQL·Redis·Kafka를 바라보는 애플리케이션 인스턴스를 두 개 실행합니다.

```powershell
.\gradlew.bat bootRun --args="--server.port=8080"
.\gradlew.bat bootRun --args="--server.port=8081"
```

두 터미널에서 readiness를 확인합니다.

```powershell
Invoke-WebRequest http://localhost:8080/actuator/health/readiness
Invoke-WebRequest http://localhost:8081/actuator/health/readiness
```

이전 대기열 데이터를 비웁니다. 이 명령은 로컬 Redis의 테스트 키만 삭제합니다.

```powershell
docker exec hhplus-local-redis redis-cli DEL queue:token
```

## 2. Prometheus·Grafana 실행

```powershell
docker compose -f loadtest/docker-compose.yml up -d
```

Grafana는 `http://localhost:3000`에서 `admin/admin`으로 접속합니다. `k6 Queue Dequeue` 대시보드는 datasource와 함께 자동으로 등록됩니다.

## 3. k6 실행

k6는 1,000명을 첫 번째 인스턴스의 enqueue API로 등록한 뒤, 10개 VU가 총 1,000회의 dequeue 요청을 두 인스턴스에 번갈아 보냅니다.

```powershell
$env:K6_PROMETHEUS_RW_SERVER_URL = "http://localhost:9090/api/v1/write"
$env:K6_PROMETHEUS_RW_TREND_STATS = "p(95),p(99),avg,max"
$env:BASE_URLS = "http://localhost:8080,http://localhost:8081"
$env:QUEUE_SIZE = "1000"
$env:VUS = "10"
$env:TEST_ID = "queue-$(Get-Date -Format yyyyMMdd-HHmmss)"
k6 run -o experimental-prometheus-rw --tag testid=$env:TEST_ID loadtest/k6/queue-dequeue.js
```

## 4. 기록할 값

- `http_reqs` 기준 dequeue 처리량
- dequeue `p(95)`, `p(99)` 응답 시간
- HTTP 실패율
- `queue_length_after = 0`
- 인스턴스별 요청 분포(`app_instance` 태그)
- 중복 dequeue: `TokenQueueMultiInstanceIntegrationTest` 결과로 별도 기록

`queue_length_after = 0`은 큐가 비었다는 뜻이지, 사용자별 중복이 없다는 뜻은 아닙니다. 따라서 API 계약을 유지하는 조건에서는 k6/Grafana를 성능 측정에 사용하고, 사용자 ID 단위 정합성은 통합 테스트로 확인합니다.

## 5. 종료

```powershell
docker compose -f loadtest/docker-compose.yml down
```
