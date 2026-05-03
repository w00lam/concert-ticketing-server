package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Implements the concert repository output port with JPA.
 */

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepositoryPort {
    private final JpaSeatRepository jpa;

    @Override
    public Seat save(Seat seat) {
        return jpa.save(seat);
    }

    @Override
    public Seat findById(UUID seatId) {
        return jpa.findById(seatId).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SEAT_NOT_FOUND, "좌석을 찾을 수 없습니다: " + seatId));
    }

    @Override
    public List<Seat> findSeatsByConcertDateId(UUID concertDateId) {
        return jpa.findAllByConcertDate_Id(concertDateId);
    }

    @Override
    public List<Seat> findSeatsByConcertDateIdForHoldRelease(LocalDateTime now) {
        return jpa.findByHoldUntilBeforeAndDeletedFalse(now);
    }
}
