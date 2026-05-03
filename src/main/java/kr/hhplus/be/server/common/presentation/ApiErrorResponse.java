package kr.hhplus.be.server.common.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
/**
 * Represents a failed API response with status, message, code, and data.
 */

@Schema(description = "공통 에러 응답")
public record ApiErrorResponse(
        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,
        @Schema(description = "에러 메시지", example = "요청 본문은 필수입니다.")
        String message,
        @Schema(description = "에러 코드", example = "REQUEST_BODY_REQUIRED")
        String code,
        @Schema(description = "에러 응답에서는 항상 null")
        Void data
) {
    public static ApiErrorResponse of(HttpStatus status, ErrorCode errorCode, String message) {
        // Error responses mirror successful responses while keeping a machine-readable code.
        return new ApiErrorResponse(status.value(), message, errorCode.name(), null);
    }
}
