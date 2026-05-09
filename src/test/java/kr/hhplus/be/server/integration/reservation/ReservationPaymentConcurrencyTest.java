package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ReservationPaymentConcurrencyTest extends ReservationIntegrationTestBase {
    @Test
    void concurrentReserveAndPayForSameSeat_allowsOnlyOnePaidReservation() throws Exception {
        User user1 = createUserWithPoints(1_000);
        User user2 = createUserWithPoints(1_000);
        User user3 = createUserWithPoints(1_000);
        List<User> users = List.of(user1, user2, user3);

        Seat seat = createSeat();
        UUID concertId = seat.getConcertDate().getConcert().getId();
        UUID seatId = seat.getId();

        int threadCount = users.size();
        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            User user = users.get(index);
            try {
                var reservation = reserveSeat(user.getId(), concertId, seatId);
                payReservation(reservation.reservationId(), 500, PaymentMethod.CARD);

                return Optional.of(reservation.reservationId());
            } catch (BusinessRuleViolationException expectedRaceLoss) {
                // Another request may reserve and pay the seat first.
                return Optional.<UUID>empty();
            }
        });
        var paidReservationIds = result.flatMapSuccesses(Optional::stream);

        em.clear();

        assertThat(result.failures()).isEmpty();
        assertThat(paidReservationIds).hasSize(1);

        UUID paidReservationId = paidReservationIds.get(0);
        assertThat(countPaymentsByReservationId(paidReservationId)).isEqualTo(1);
        assertThat(countReservationsBySeatAndStatus(seat, ReservationStatus.CONFIRMED)).isEqualTo(1);

        List<Integer> balances = users.stream()
                .map(user -> userRepository.findById(user.getId()).getPoints())
                .toList();

        assertThat(balances).containsExactlyInAnyOrder(500, 1_000, 1_000);
    }
}
