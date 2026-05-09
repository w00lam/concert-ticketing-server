package kr.hhplus.be.server.payment.domain.model;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public final class PaymentExceptions {
    private static final String ALREADY_PROCESSED_MESSAGE = "이미 결제된 예약입니다.";

    private PaymentExceptions() {
    }

    public static BusinessRuleViolationException alreadyProcessed() {
        return new BusinessRuleViolationException(
                ErrorCode.PAYMENT_ALREADY_PROCESSED,
                ALREADY_PROCESSED_MESSAGE
        );
    }
}
