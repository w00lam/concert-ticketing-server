# Local Development Guide

이 문서는 로컬에서 애플리케이션을 실행하기 위한 인프라 준비 순서를 정리합니다.

## Prerequisites

- Java 17
- Docker Desktop 또는 Docker Engine
- Gradle Wrapper

## Start Infrastructure

MySQL, Redis, Kafka를 Docker Compose로 실행합니다.

```bash
docker compose up -d
```

컨테이너 상태를 확인합니다.

```bash
docker compose ps
```

## Run Application

애플리케이션은 IDE 또는 Gradle로 실행합니다.

```bash
./gradlew bootRun
```

로컬 프로필은 다음 인프라 주소를 사용합니다.

| Dependency | Address |
| --- | --- |
| MySQL | `localhost:3306` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |

## API Docs

애플리케이션 실행 후 다음 주소에서 API 문서를 확인합니다.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Stop Infrastructure

컨테이너만 종료합니다.

```bash
docker compose down
```

로컬 데이터를 함께 삭제하려면 볼륨까지 제거합니다.

```bash
docker compose down -v
```
