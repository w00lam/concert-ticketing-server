package kr.hhplus.be.server.common.presentation;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

/**
 * Represents a successful API response with status, message, and data.
 */
@Schema(description = "공통 성공 응답")
public record ApiResponse<T>(
        @Schema(description = "HTTP 상태 코드", example = "200")
        int status,
        @Schema(description = "성공 메시지", example = "요청이 성공했습니다.")
        String message,
        @Schema(description = "응답 데이터")
        T data
) {
    private static final String SUCCESS_MESSAGE = "요청이 성공했습니다.";

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), SUCCESS_MESSAGE, data);
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }
}
