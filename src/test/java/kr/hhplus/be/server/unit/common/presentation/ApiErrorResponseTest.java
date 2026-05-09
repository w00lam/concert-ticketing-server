package kr.hhplus.be.server.unit.common.presentation;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.presentation.ApiErrorResponse;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiErrorResponseTest extends BaseUnitTest {

    @Test
    @DisplayName("of: wraps error status, message, code and empty errors")
    void of() {
        var response = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");

        assertEquals(400, response.status());
        assertEquals("요청 본문은 필수입니다.", response.message());
        assertEquals(ErrorCode.REQUEST_BODY_REQUIRED.name(), response.code());
        assertTrue(response.errors().isEmpty());
    }

    @Test
    @DisplayName("of: copies error details")
    void of_withErrors() {
        var detail = new ApiErrorResponse.ErrorDetail("amount", "0보다 커야 합니다.");

        var response = ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_REQUEST_FIELD,
                "요청 필드 값이 올바르지 않습니다.",
                List.of(detail)
        );

        assertEquals(List.of(detail), response.errors());
    }
}
