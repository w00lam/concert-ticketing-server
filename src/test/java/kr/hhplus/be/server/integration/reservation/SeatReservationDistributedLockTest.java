package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatReservationDistributedLockTest extends ReservationIntegrationTestBase {
    @Test
    @DisplayName("동시에 여러 사용자가 동일 좌석 예약 시도 시 하나만 성공한다 (Redis 분산락)")
    void only_one_user_can_hold_seat_with_distributed_lock() throws Exception {
        int threadCount = 10;
        var concert = Concert.builder().title("concert").build();
        var seat = createSeat();

        List<UUID> userIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            userIds.add(createUser().getId());
        }

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                reserveSeat(userIds.get(index), concert.getId(), seat.getId());
                return true;
            } catch (Exception exception) {
                return false;
            }
        });

        assertThat(result.failures()).isEmpty();
        assertThat(result.matchingSuccessCount(Boolean::booleanValue)).isEqualTo(1);
        assertThat(countReservationsBySeatAndStatus(seat, ReservationStatus.TEMP_HOLD)).isEqualTo(1);
    }
}
