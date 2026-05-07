package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
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
    }

    @Test
    void differentDuplicatePaymentRequest_isRejected() {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createReservation(user);

        makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 5_000, PaymentMethod.CARD));

        assertThatThrownBy(() ->
                makePaymentUseCase.execute(new MakePaymentCommand(reservationId, 6_000, PaymentMethod.CARD))
        ).isInstanceOf(BusinessRuleViolationException.class);

        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);
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
