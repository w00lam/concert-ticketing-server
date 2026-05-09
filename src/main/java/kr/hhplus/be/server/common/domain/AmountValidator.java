package kr.hhplus.be.server.common.domain;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public final class AmountValidator {
    private static final String POSITIVE_AMOUNT_MESSAGE = "\uAE08\uC561\uC740 0\uBCF4\uB2E4 \uCEE4\uC57C \uD569\uB2C8\uB2E4.";
    private static final String NON_NEGATIVE_AMOUNT_MESSAGE = "\uAE08\uC561\uC740 \uC74C\uC218\uC77C \uC218 \uC5C6\uC2B5\uB2C8\uB2E4.";

    private AmountValidator() {
    }

    public static void requirePositive(int amount) {
        if (amount <= 0) {
            throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_POSITIVE, POSITIVE_AMOUNT_MESSAGE);
        }
    }

    public static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_NON_NEGATIVE, NON_NEGATIVE_AMOUNT_MESSAGE);
        }
    }
}
