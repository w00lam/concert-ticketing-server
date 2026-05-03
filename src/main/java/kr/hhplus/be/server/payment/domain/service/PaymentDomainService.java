package kr.hhplus.be.server.payment.domain.service;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import org.springframework.stereotype.Component;
/**
 * Encapsulates domain rules for the payment feature.
 */

@Component
public class PaymentDomainService {
    public void validateAmount(int amount) {
        if (amount <= 0) throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_POSITIVE, "금액은 0보다 커야 합니다.");
    }

    public Payment createPending(Reservation reservation, int amount, PaymentMethod method) {
        return Payment.createPending(reservation, amount, method);
    }
}
