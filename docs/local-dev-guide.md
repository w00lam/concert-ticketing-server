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

애플리케이션은 IDE 또는 Gradle로 실행합니다. 별도 프로필을 지정하지 않으면 `local` 프로필이 기본으로 사용됩니다.

```bash
./gradlew bootRun
```

로컬 프로필은 다음 인프라 주소를 사용합니다.

| Dependency | Address |
| --- | --- |
| MySQL | `localhost:3306` |
| Redis | `localhost:6379` |
| Kafka | `localhost:9092` |

운영 환경처럼 명시적으로 프로필을 지정하려면 다음과 같이 실행합니다.

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```

PowerShell에서는 다음과 같이 실행합니다.

```powershell
$env:SPRING_PROFILES_ACTIVE="local"; .\gradlew.bat bootRun
```

운영 프로필에서 필요한 환경변수 목록은 프로젝트 루트의 `.env.example`을 참고합니다. 실제 `.env` 파일은 Git에 포함하지 않습니다.

## API Docs

애플리케이션 실행 후 다음 주소에서 API 문서를 확인합니다.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Health Check

로컬 실행 후 Actuator health endpoint로 애플리케이션 상태를 확인합니다.

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
```

## Stop Infrastructure

컨테이너만 종료합니다.

```bash
docker compose down
```

로컬 데이터를 함께 삭제하려면 볼륨까지 제거합니다.

```bash
docker compose down -v
```
