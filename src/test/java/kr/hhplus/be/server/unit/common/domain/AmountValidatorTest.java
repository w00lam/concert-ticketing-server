package kr.hhplus.be.server.unit.common.domain;

import kr.hhplus.be.server.common.domain.AmountValidator;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AmountValidatorTest extends BaseUnitTest {
    @Test
    void requirePositive_rejectsZeroAndNegativeAmounts() {
        ClientInputException zero = assertThrows(ClientInputException.class, () -> AmountValidator.requirePositive(0));
        ClientInputException negative = assertThrows(ClientInputException.class, () -> AmountValidator.requirePositive(-1));

        assertEquals(ErrorCode.AMOUNT_MUST_BE_POSITIVE, zero.errorCode());
        assertEquals(ErrorCode.AMOUNT_MUST_BE_POSITIVE, negative.errorCode());
    }

    @Test
    void requirePositive_acceptsPositiveAmounts() {
        assertDoesNotThrow(() -> AmountValidator.requirePositive(1));
    }

    @Test
    void requireNonNegative_rejectsNegativeAmounts() {
        ClientInputException exception = assertThrows(
                ClientInputException.class,
                () -> AmountValidator.requireNonNegative(-1)
        );

        assertEquals(ErrorCode.AMOUNT_MUST_BE_NON_NEGATIVE, exception.errorCode());
    }

    @Test
    void requireNonNegative_acceptsZeroAndPositiveAmounts() {
        assertDoesNotThrow(() -> AmountValidator.requireNonNegative(0));
        assertDoesNotThrow(() -> AmountValidator.requireNonNegative(1));
    }
}
