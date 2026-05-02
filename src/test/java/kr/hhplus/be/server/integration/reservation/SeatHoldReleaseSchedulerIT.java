package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.application.service.SeatHoldReleaseScheduler;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SeatHoldReleaseSchedulerIT extends ReservationIntegrationTestBase {
    @Autowired
    private SeatHoldReleaseScheduler scheduler;

    @Test
    void testSchedulerReleasesExpiredSeat() {
        // given: 만료된 좌석 생성
        Seat seat = createSeat();
        seat.setHeld(true);
        seat.setHoldUntil(LocalDateTime.now().minusMinutes(1)); // 이미 만료
        seatRepository.save(seat);

        // when: 스케줄러 실행
        scheduler.releaseExpiredHolds();

        // then: deleted=false로 변경 확인
        Seat updatedSeat = seatRepository.findById(seat.getId());
        assertFalse(updatedSeat.isHeld());
        assertNull(updatedSeat.getHoldUntil());
    }
}
