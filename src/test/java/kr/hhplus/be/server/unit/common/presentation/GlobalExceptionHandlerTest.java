package kr.hhplus.be.server.unit.common.presentation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.common.presentation.GlobalExceptionHandler;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GlobalExceptionHandlerTest extends BaseUnitTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleClientInput: returns bad request problem with stable code")
    void handleClientInput() {
        var problem = handler.handleClientInput(new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다."));

        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertEquals("요청 본문은 필수입니다.", problem.getDetail());
        assertEquals(ErrorCode.REQUEST_BODY_REQUIRED.name(), problem.getProperties().get("code"));
    }

    @Test
    @DisplayName("handleResourceNotFound: returns not found problem with stable code")
    void handleResourceNotFound() {
        var problem = handler.handleResourceNotFound(new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        assertEquals(HttpStatus.NOT_FOUND.value(), problem.getStatus());
        assertEquals(ErrorCode.USER_NOT_FOUND.name(), problem.getProperties().get("code"));
    }

    @Test
    @DisplayName("handleBusinessRuleViolation: returns conflict problem with stable code")
    void handleBusinessRuleViolation() {
        var problem = handler.handleBusinessRuleViolation(new BusinessRuleViolationException(ErrorCode.RESERVATION_ALREADY_CANCELLED, "이미 취소된 예약입니다."));

        assertEquals(HttpStatus.CONFLICT.value(), problem.getStatus());
        assertEquals("이미 취소된 예약입니다.", problem.getDetail());
        assertEquals(ErrorCode.RESERVATION_ALREADY_CANCELLED.name(), problem.getProperties().get("code"));
    }
}
