package kr.hhplus.be.server.unit.common.presentation;

import kr.hhplus.be.server.common.presentation.ApiResponse;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ApiResponseTest extends BaseUnitTest {

    @Test
    @DisplayName("ok: wraps data with success status and message")
    void okWithData() {
        var response = ApiResponse.ok("value");

        assertEquals(200, response.status());
        assertEquals("요청이 성공했습니다.", response.message());
        assertEquals("value", response.data());
    }

    @Test
    @DisplayName("ok: wraps void response with null data")
    void okWithoutData() {
        var response = ApiResponse.ok();

        assertEquals(200, response.status());
        assertEquals("요청이 성공했습니다.", response.message());
        assertNull(response.data());
    }
}
