package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class PaymentConcurrencyIntegrationTest extends ReservationIntegrationTestBase {
    @ParameterizedTest(name = "동일 결제 요청 {0}건은 결제와 포인트를 한 번만 반영한다")
    @ValueSource(ints = {100, 500})
    void concurrentSamePaymentRequest_createsOnePaymentAndDeductsPointsOnce(int threadCount) throws Exception {
        User user = createUserWithPoints(10_000);
        UUID reservationId = createReservedSeatId(user, "payment concurrency concert");
        var command = cardPaymentCommand(reservationId, 5_000);

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                return Optional.of(makePaymentUseCase.execute(command).paymentId());
            } catch (BusinessRuleViolationException expectedRaceLoss) {
                // Another transaction may confirm the reservation first.
                return Optional.<UUID>empty();
            }
        });
        var successfulPaymentIds = result.flatMapSuccesses(Optional::stream);

        em.clear();

        assertThat(result.failures()).isEmpty();
        assertThat(successfulPaymentIds).isNotEmpty();
        assertThat(successfulPaymentIds.stream().distinct()).hasSize(1);
        assertThat(countPaymentsByReservationId(reservationId)).isEqualTo(1);
        assertThat(userRepository.findById(user.getId()).getPoints()).isEqualTo(5_000);
        assertThat(reservationRepository.findById(reservationId).getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }
}
