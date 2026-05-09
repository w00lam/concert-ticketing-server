package kr.hhplus.be.server.common.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Represents a failed API response with a stable code and optional error details.
 */
@Schema(description = "공통 에러 응답")
public record ApiErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,
        @Schema(description = "에러 메시지", example = "요청 본문은 필수입니다.")
        String message,
        @Schema(description = "에러 코드", example = "REQUEST_BODY_REQUIRED")
        String code,
        @Schema(description = "필드별 오류 상세. 상세 오류가 없으면 빈 배열입니다.")
        List<ErrorDetail> errors
) {
    public static ApiErrorResponse of(HttpStatus status, ErrorCode errorCode, String message) {
        return of(status, errorCode, message, List.of());
    }

    public static ApiErrorResponse of(
            HttpStatus status,
            ErrorCode errorCode,
            String message,
            List<ErrorDetail> errors
    ) {
        return new ApiErrorResponse(status.value(), message, errorCode.name(), List.copyOf(errors));
    }

    @Schema(description = "필드 오류 상세")
    public record ErrorDetail(
            @Schema(description = "오류가 발생한 필드", example = "amount")
            String field,
            @Schema(description = "필드 오류 메시지", example = "0보다 커야 합니다.")
            String message
    ) {
    }
}
