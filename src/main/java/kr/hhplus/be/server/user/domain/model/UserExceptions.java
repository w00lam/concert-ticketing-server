package kr.hhplus.be.server.user.domain.model;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public final class UserExceptions {
    private static final String INSUFFICIENT_POINTS_MESSAGE = "포인트가 부족합니다.";

    private UserExceptions() {
    }

    public static BusinessRuleViolationException insufficientPoints() {
        return new BusinessRuleViolationException(
                ErrorCode.INSUFFICIENT_POINTS,
                INSUFFICIENT_POINTS_MESSAGE
        );
    }
}
