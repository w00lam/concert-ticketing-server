package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class SeatReservationDistributedLockTest extends ReservationIntegrationTestBase {
    @Test
    @DisplayName("동시에 여러 사용자가 동일 좌석 예약 시도 시 하나만 성공한다 (Redis 분산락)")
    void only_one_user_can_hold_seat_with_distributed_lock() throws Exception {
        // given
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        var concert = Concert.builder().title("concert").build();
        var seat = createSeat();

        List<UUID> userIds = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            userIds.add(createUser().getId());
        }

        List<Future<Boolean>> results = new ArrayList<>();

        // when
        for (UUID userId : userIds) {
            results.add(
                    executor.submit(() -> {
                        try {
                            reserveSeat(userId, concert.getId(), seat.getId());
                            return true;
                        } catch (Exception exception) {
                            return false;
                        } finally {
                            latch.countDown();
                        }
                    })
            );
        }

        latch.await();
        executor.shutdown();

        // then
        long successCount = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                successCount++;
            }
        }

        assertThat(successCount).isEqualTo(1);

        long count = countReservationsBySeatAndStatus(seat, ReservationStatus.TEMP_HOLD);

        assertThat(count).isEqualTo(1);

    }
}
