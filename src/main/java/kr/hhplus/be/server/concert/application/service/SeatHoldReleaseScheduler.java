package kr.hhplus.be.server.concert.application.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatHoldReleaseScheduler {
    private final SeatRepositoryPort seatRepositoryPort;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<Seat> seats = seatRepositoryPort.findSeatsByConcertDateIdForHoldRelease(now);

        for (Seat seat : seats) {
            seat.setHoldUntil(null);
            seat.setHeld(false);
            seatRepositoryPort.save(seat);
        }
    }
}
