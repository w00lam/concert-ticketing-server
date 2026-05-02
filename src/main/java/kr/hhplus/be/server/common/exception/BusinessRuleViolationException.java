package kr.hhplus.be.server.common.exception;

public class BusinessRuleViolationException extends IllegalStateException implements CodedException {
    public BusinessRuleViolationException(String message) {
        // Business rule violations represent valid requests blocked by current domain state.
        super(message);
    }

    @Override
    public ErrorCode errorCode() {
        return ErrorCode.BUSINESS_RULE_VIOLATION;
    }
}
