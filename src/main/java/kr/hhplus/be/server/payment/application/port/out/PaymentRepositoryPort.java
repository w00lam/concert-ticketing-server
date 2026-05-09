package kr.hhplus.be.server.payment.application.port.out;

import kr.hhplus.be.server.payment.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;
/**
 * Defines the output port for payment persistence.
 */

public interface PaymentRepositoryPort {
    Optional<Payment> findByReservationId(UUID reservationId);
    Payment save(Payment payment);
}
