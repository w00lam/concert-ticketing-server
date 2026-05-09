package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;

import java.util.List;
import java.util.UUID;
/**
 * Defines the output port for concert persistence.
 */

public interface SeatRepositoryPort {
    Seat save(Seat seat);
    Seat findById(UUID seatId);
    List<GetSeatsResult> findSeatResultsByConcertDateId(UUID concertDateId);
}
