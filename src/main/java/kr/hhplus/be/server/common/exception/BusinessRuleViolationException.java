package kr.hhplus.be.server.common.exception;

public class BusinessRuleViolationException extends IllegalStateException implements CodedException {
    private final ErrorCode errorCode;

    public BusinessRuleViolationException(ErrorCode errorCode, String message) {
        // Business rule violations represent valid requests blocked by current domain state.
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode errorCode() {
        return errorCode;
    }
}
