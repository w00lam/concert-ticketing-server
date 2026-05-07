package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<UUID> paidReservationIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Throwable> unexpectedFailures = new ConcurrentLinkedQueue<>();

        for (User user : users) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    var reservation = makeReservationUseCase.execute(
                            new MakeReservationCommand(user.getId(), concertId, seatId)
                    );
                    makePaymentUseCase.execute(
                            new MakePaymentCommand(reservation.reservationId(), 500, PaymentMethod.CARD)
                    );

                    paidReservationIds.add(reservation.reservationId());
                } catch (BusinessRuleViolationException expectedRaceLoss) {
                    // Another request may reserve and pay the seat first.
                } catch (Exception exception) {
                    unexpectedFailures.add(exception);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        em.clear();

        assertThat(unexpectedFailures).isEmpty();
        assertThat(paidReservationIds).hasSize(1);

        UUID paidReservationId = paidReservationIds.iterator().next();
        assertThat(countPaymentsByReservationId(paidReservationId)).isEqualTo(1);
        assertThat(countReservationsBySeatAndStatus(seat, ReservationStatus.CONFIRMED)).isEqualTo(1);

        List<Integer> balances = users.stream()
                .map(user -> userRepository.findById(user.getId()).getPoints())
                .toList();

        assertThat(balances).containsExactlyInAnyOrder(500, 1_000, 1_000);
    }
}
