package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaymentIdempotencyIntegrationTest extends ReservationIntegrationTestBase {
    @Test
    void samePaymentRequest_returnsExistingPayment() {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createPaymentIdempotencyReservation(user);
        MakePaymentCommand command = cardPaymentCommand(reservationId, 5_000);

        var first = makePaymentUseCase.execute(command);
        var second = makePaymentUseCase.execute(command);

        assertThat(second.paymentId()).isEqualTo(first.paymentId());
        assertThat(second.status()).isEqualTo(first.status());
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
    }

    @Test
    void differentDuplicatePaymentRequest_isRejected() {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createPaymentIdempotencyReservation(user);

        makePaymentUseCase.execute(cardPaymentCommand(reservationId, 5_000));

        assertThatThrownBy(() ->
                makePaymentUseCase.execute(cardPaymentCommand(reservationId, 6_000))
        ).isInstanceOfSatisfying(BusinessRuleViolationException.class, exception ->
                assertThat(exception.errorCode()).isEqualTo(ErrorCode.PAYMENT_ALREADY_PROCESSED)
        );

        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
    }

    @Test
    void successfulPayment_deductsPointsAndConfirmsReservation() {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createPaymentIdempotencyReservation(user);

        makePaymentUseCase.execute(cardPaymentCommand(reservationId, 5_000));

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);
    }

    @Test
    void insufficientPoints_doesNotConfirmReservationOrCreatePayment() {
        User user = createUserWithPoints(1_000);
        UUID reservationId = createPaymentIdempotencyReservation(user);

        assertThatThrownBy(() ->
                makePaymentUseCase.execute(cardPaymentCommand(reservationId, 5_000))
        ).isInstanceOfSatisfying(BusinessRuleViolationException.class, exception ->
                assertThat(exception.errorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINTS)
        );

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(1_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.TEMP_HOLD);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(0);
    }

    private UUID createPaymentIdempotencyReservation(User user) {
        return createReservedSeatId(user, "payment idempotency concert");
    }
}
