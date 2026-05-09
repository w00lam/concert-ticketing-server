package kr.hhplus.be.server.payment.infrastructure.persistence;

import kr.hhplus.be.server.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
/**
 * Spring Data JPA repository for payment entities.
 */

@Repository
public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReservation_Id(UUID reservationId);
}
