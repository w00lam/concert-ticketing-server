package kr.hhplus.be.server.payment.application.port.out;

import kr.hhplus.be.server.payment.domain.model.Payment;
/**
 * Defines the output port for payment persistence.
 */

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
}
