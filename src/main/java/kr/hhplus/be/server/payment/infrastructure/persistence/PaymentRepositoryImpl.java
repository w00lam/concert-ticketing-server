package kr.hhplus.be.server.payment.infrastructure.persistence;

import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
/**
 * Implements the payment repository output port with JPA.
 */

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepositoryPort {
    private final JpaPaymentRepository jpa;

    @Override
    public Optional<Payment> findByReservationId(UUID reservationId) {
        return jpa.findByReservation_Id(reservationId);
    }

    @Override
    public Payment save(Payment payment) {
        return jpa.save(payment);
    }
}
