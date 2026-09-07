package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.common.application.lock.DistributedLockManager;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SeatReservationWithoutLockComparisonTest.NoLockConfiguration.class)
public class SeatReservationWithoutLockComparisonTest extends ReservationIntegrationTestBase {
    @ParameterizedTest(name = "락 없이 동일 좌석 {0}건 요청 결과를 기록한다")
    @ValueSource(ints = {100, 500})
    void recordsReservationCountWithoutDistributedLock(int threadCount) throws Exception {
        Seat seat = createSeat();
        List<UUID> userIds = new ArrayList<>();
        for (int index = 0; index < threadCount; index++) {
            userIds.add(createUser().getId());
        }

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                reserveSeat(userIds.get(index), seat.getConcertDate().getConcert().getId(), seat.getId());
                return true;
            } catch (BusinessRuleViolationException expectedRaceLoss) {
                return false;
            }
        });

        long reservationCount = countReservationsBySeatAndStatus(seat, ReservationStatus.TEMP_HOLD);

        assertThat(result.failures()).isEmpty();
        assertThat(reservationCount).isGreaterThanOrEqualTo(1);
        System.out.printf(
                "lock=disabled, thread_count=%d, successful_requests=%d, reservations=%d%n",
                threadCount,
                result.matchingSuccessCount(Boolean::booleanValue),
                reservationCount
        );
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class NoLockConfiguration {
        @Bean
        @Primary
        DistributedLockManager noOpDistributedLockManager() {
            return new DistributedLockManager() {
                @Override
                public String lock(String key, Duration ttl) {
                    return UUID.randomUUID().toString();
                }

                @Override
                public void unlock(String key, String lockValue) {
                    // Deliberately skip lock release in the no-lock comparison profile.
                }
            };
        }
    }
}
