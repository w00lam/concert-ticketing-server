package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SeatRepositoryPort {
    Seat save(Seat seat);
    Seat findById(UUID seatId);
    List<Seat> findSeatsByConcertDateId(UUID concertDateId);
    List<Seat> findSeatsByConcertDateIdForHoldRelease(LocalDateTime now);
}
