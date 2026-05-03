package kr.hhplus.be.server.unit.common.presentation;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.presentation.ApiErrorResponse;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApiErrorResponseTest extends BaseUnitTest {

    @Test
    @DisplayName("of: wraps error status, message, code and null data")
    void of() {
        var response = ApiErrorResponse.of(HttpStatus.BAD_REQUEST, ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");

        assertEquals(400, response.status());
        assertEquals("요청 본문은 필수입니다.", response.message());
        assertEquals(ErrorCode.REQUEST_BODY_REQUIRED.name(), response.code());
        assertNull(response.data());
    }
}
