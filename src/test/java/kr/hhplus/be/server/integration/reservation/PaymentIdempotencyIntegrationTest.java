package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaymentIdempotencyIntegrationTest extends ReservationIntegrationTestBase {
    @Test
    void samePaymentRequest_returnsExistingPayment() {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createReservation(user);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD);

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
        UUID reservationId = createReservation(user);

        makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD));

        assertThatThrownBy(() ->
                makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 6_000, PaymentMethod.CARD))
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
        UUID reservationId = createReservation(user);

        makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD));

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);
    }

    @Test
    void insufficientPoints_doesNotConfirmReservationOrCreatePayment() {
        User user = createUserWithPoints(1_000);
        UUID reservationId = createReservation(user);

        assertThatThrownBy(() ->
                makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD))
        ).isInstanceOfSatisfying(BusinessRuleViolationException.class, exception ->
                assertThat(exception.errorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINTS)
        );

        em.clear();
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(1_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.TEMP_HOLD);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(0);
    }

    private UUID createReservation(User user) {
        Concert concert = concertRepository.save(
                Concert.builder()
                        .title("payment idempotency concert")
                        .build()
        );
        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");

        return reserveSeat(user.getId(), concert.getId(), seat.getId()).reservationId();
    }
}
