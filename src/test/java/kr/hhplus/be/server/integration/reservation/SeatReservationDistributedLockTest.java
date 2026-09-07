package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatReservationDistributedLockTest extends ReservationIntegrationTestBase {
    @ParameterizedTest(name = "Redis 분산락은 동일 좌석 {0}건 요청에서도 하나만 성공시킨다")
    @ValueSource(ints = {100, 500})
    void only_one_user_can_hold_seat_with_distributed_lock(int threadCount) throws Exception {
        var seat = createSeat();

        List<UUID> userIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            userIds.add(createUser().getId());
        }

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                reserveSeat(userIds.get(index), seat.getConcertDate().getConcert().getId(), seat.getId());
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
