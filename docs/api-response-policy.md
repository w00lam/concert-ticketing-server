# API Response Policy

이 문서는 티켓팅 서비스의 HTTP API 응답 표준을 정의한다. 포트폴리오 관점에서는 API 사용자가 성공/실패 응답을 일관되게 해석할 수 있도록 만든 응답 계약이다.

## Success Response

성공 응답은 모든 컨트롤러에서 `ApiResponse<T>`로 감싼다.

```json
{
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": {}
}
```

필드 설명:

| Field | Type | Description |
| --- | --- | --- |
| `status` | number | HTTP 상태 코드 |
| `message` | string | 사용자 또는 클라이언트가 읽을 수 있는 성공 메시지 |
| `data` | object, array, primitive, null | 실제 응답 데이터 |

데이터가 없는 명령 API도 동일한 형태를 유지한다.

```json
{
  "status": 200,
  "message": "요청이 성공했습니다.",
  "data": null
}
```

## Error Response

실패 응답은 `GlobalExceptionHandler`에서 `ApiErrorResponse`로 통일한다.

```json
{
  "status": 400,
  "message": "요청 본문은 필수입니다.",
  "code": "REQUEST_BODY_REQUIRED",
  "data": null
}
```

필드 설명:

| Field | Type | Description |
| --- | --- | --- |
| `status` | number | HTTP 상태 코드 |
| `message` | string | 한글 오류 메시지 |
| `code` | string | 클라이언트 분기를 위한 안정적인 에러 코드 |
| `data` | null | 에러 응답에서는 항상 `null` |

## HTTP Status Mapping

| Exception | Status | Description |
| --- | --- | --- |
| `ClientInputException` | `400 Bad Request` | 클라이언트 요청 값이 잘못된 경우 |
| `MethodArgumentNotValidException` | `400 Bad Request` | Bean Validation 검증에 실패한 경우 |
| `HttpMessageNotReadableException` | `400 Bad Request` | JSON 본문 파싱 또는 역직렬화에 실패한 경우 |
| `MethodArgumentTypeMismatchException` | `400 Bad Request` | PathVariable 또는 request parameter 타입이 맞지 않는 경우 |
| `ResourceNotFoundException` | `404 Not Found` | 도메인 리소스를 찾을 수 없는 경우 |
| `NoResourceFoundException` | `404 Not Found` | 요청한 HTTP 리소스를 찾을 수 없는 경우 |
| `BusinessRuleViolationException` | `409 Conflict` | 요청은 유효하지만 현재 도메인 상태상 처리할 수 없는 경우 |
| `Exception` | `500 Internal Server Error` | 예상하지 못한 서버 내부 오류 |

## Error Codes

### Request Validation

| Code | Message Example | Description |
| --- | --- | --- |
| `REQUEST_BODY_REQUIRED` | 요청 본문은 필수입니다. | 요청 본문이 필요한 API에서 body가 없는 경우 |
| `USER_ID_REQUIRED` | 사용자 ID는 필수입니다. | 사용자 ID가 누락된 경우 |
| `RESERVATION_ID_REQUIRED` | 예약 ID는 필수입니다. | 예약 ID가 누락된 경우 |
| `CONCERT_ID_REQUIRED` | 콘서트 ID는 필수입니다. | 콘서트 ID가 누락된 경우 |
| `SEAT_ID_REQUIRED` | 좌석 ID는 필수입니다. | 좌석 ID가 누락된 경우 |
| `AMOUNT_MUST_BE_POSITIVE` | 금액은 0보다 커야 합니다. | 금액이 0 이하인 경우 |
| `AMOUNT_MUST_BE_NON_NEGATIVE` | 금액은 음수일 수 없습니다. | 금액이 음수인 경우 |
| `PAYMENT_METHOD_REQUIRED` | 결제 수단은 필수입니다. | 결제 수단이 누락된 경우 |
| `INVALID_REQUEST_BODY` | 요청 본문을 읽을 수 없습니다. | JSON 파싱 또는 enum 변환 등에 실패한 경우 |
| `INVALID_REQUEST_PARAMETER` | 요청 파라미터 형식이 올바르지 않습니다. | URL 변수나 쿼리 파라미터 타입이 잘못된 경우 |
| `INVALID_REQUEST_FIELD` | 요청 필드 값이 올바르지 않습니다. | Bean Validation 검증에 실패한 경우 |

### Resource Lookup

| Code | Message Example | Description |
| --- | --- | --- |
| `RESOURCE_NOT_FOUND` | 요청한 리소스를 찾을 수 없습니다. | HTTP 리소스를 찾을 수 없는 경우 |
| `USER_NOT_FOUND` | 사용자를 찾을 수 없습니다. | 사용자 조회에 실패한 경우 |
| `SEAT_NOT_FOUND` | 좌석을 찾을 수 없습니다. | 좌석 조회에 실패한 경우 |
| `RESERVATION_NOT_FOUND` | 예약을 찾을 수 없습니다. | 예약 조회에 실패한 경우 |

### Business Rules

| Code | Message Example | Description |
| --- | --- | --- |
| `INSUFFICIENT_POINTS` | 포인트가 부족합니다. | 보유 포인트가 결제 금액보다 적은 경우 |
| `RESERVATION_ALREADY_CANCELLED` | 이미 취소된 예약입니다. | 이미 취소된 예약을 다시 취소하려는 경우 |
| `RESERVATION_EXPIRED_OR_PROCESSED` | 예약이 만료되었거나 이미 처리되었습니다. | 예약 확정 조건을 만족하지 못한 경우 |

### Server Error

| Code | Message Example | Description |
| --- | --- | --- |
| `INTERNAL_SERVER_ERROR` | 서버 내부 오류가 발생했습니다. | 예측하지 못한 서버 오류 |

## Design Notes

- 성공 응답과 실패 응답 모두 `status`, `message`, `data`를 포함해 응답 형태를 맞춘다.
- 실패 응답에는 클라이언트가 메시지 문자열을 파싱하지 않도록 `code`를 추가한다.
- 도메인 규칙 위반은 `400`이 아니라 `409 Conflict`로 처리한다. 요청 형식은 올바르지만 현재 예약/포인트 상태 때문에 처리할 수 없는 상황이기 때문이다.
- 서버 내부 오류는 상세 예외 메시지를 노출하지 않고 일반화된 한글 메시지만 반환한다.
