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
        var problem = handler.handleClientInput(new ClientInputException("Request is required"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertEquals("Request is required", problem.getDetail());
        assertEquals(ErrorCode.CLIENT_INPUT_ERROR.name(), problem.getProperties().get("code"));
    }

    @Test
    @DisplayName("handleResourceNotFound: returns not found problem with stable code")
    void handleResourceNotFound() {
        var problem = handler.handleResourceNotFound(new ResourceNotFoundException("User", fixedUUID()));

        assertEquals(HttpStatus.NOT_FOUND.value(), problem.getStatus());
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND.name(), problem.getProperties().get("code"));
    }

    @Test
    @DisplayName("handleBusinessRuleViolation: returns conflict problem with stable code")
    void handleBusinessRuleViolation() {
        var problem = handler.handleBusinessRuleViolation(new BusinessRuleViolationException("Already cancelled"));

        assertEquals(HttpStatus.CONFLICT.value(), problem.getStatus());
        assertEquals("Already cancelled", problem.getDetail());
        assertEquals(ErrorCode.BUSINESS_RULE_VIOLATION.name(), problem.getProperties().get("code"));
    }
}
