package kr.hhplus.be.server.unit.common.presentation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.common.presentation.GlobalExceptionHandler;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GlobalExceptionHandlerTest extends BaseUnitTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleClientInput: returns bad request error response with stable code")
    void handleClientInput() {
        var response = handler.handleClientInput(new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다."));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("요청 본문은 필수입니다.", response.getBody().message());
        assertEquals(ErrorCode.REQUEST_BODY_REQUIRED.name(), response.getBody().code());
        assertNull(response.getBody().data());
    }

    @Test
    @DisplayName("handleResourceNotFound: returns not found error response with stable code")
    void handleResourceNotFound() {
        var response = handler.handleResourceNotFound(new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ErrorCode.USER_NOT_FOUND.name(), response.getBody().code());
    }

    @Test
    @DisplayName("handleBusinessRuleViolation: returns conflict error response with stable code")
    void handleBusinessRuleViolation() {
        var response = handler.handleBusinessRuleViolation(new BusinessRuleViolationException(ErrorCode.RESERVATION_ALREADY_CANCELLED, "이미 취소된 예약입니다."));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("이미 취소된 예약입니다.", response.getBody().message());
        assertEquals(ErrorCode.RESERVATION_ALREADY_CANCELLED.name(), response.getBody().code());
    }

    @Test
    @DisplayName("handleValidation: returns invalid request field code")
    void handleValidation() throws NoSuchMethodException {
        Method method = ValidationFixture.class.getDeclaredMethod("validate", String.class);
        var methodParameter = new MethodParameter(method, 0);
        var bindingResult = new BeanPropertyBindingResult(new ValidationFixture(), "request");
        bindingResult.addError(new FieldError("request", "name", "must not be blank"));
        var exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        var response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("요청 필드 값이 올바르지 않습니다.", response.getBody().message());
        assertEquals(ErrorCode.INVALID_REQUEST_FIELD.name(), response.getBody().code());
    }

    @Test
    @DisplayName("handleUnreadableMessage: returns invalid request body code")
    void handleUnreadableMessage() {
        var exception = new HttpMessageNotReadableException("invalid json", new MockHttpInputMessage(new byte[0]));

        var response = handler.handleUnreadableMessage(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("요청 본문을 읽을 수 없습니다.", response.getBody().message());
        assertEquals(ErrorCode.INVALID_REQUEST_BODY.name(), response.getBody().code());
    }

    @Test
    @DisplayName("handleTypeMismatch: returns invalid request parameter code")
    void handleTypeMismatch() {
        var exception = new MethodArgumentTypeMismatchException("abc", Integer.class, "amount", null, null);

        var response = handler.handleTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("요청 파라미터 형식이 올바르지 않습니다.", response.getBody().message());
        assertEquals(ErrorCode.INVALID_REQUEST_PARAMETER.name(), response.getBody().code());
    }

    @Test
    @DisplayName("handleUnexpected: returns internal server error code")
    void handleUnexpected() {
        var response = handler.handleUnexpected(new RuntimeException("secret"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("서버 내부 오류가 발생했습니다.", response.getBody().message());
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.name(), response.getBody().code());
    }

    private static class ValidationFixture {
        void validate(String name) {
        }
    }
}
