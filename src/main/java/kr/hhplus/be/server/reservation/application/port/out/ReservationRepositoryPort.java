package kr.hhplus.be.server.reservation.application.port.out;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;

import java.util.UUID;
/**
 * Defines the output port for reservation persistence.
 */

public interface ReservationRepositoryPort {
    Reservation findById(UUID reservationId);

    boolean confirmIfNotExpired(UUID reservationId);

    Reservation save(Reservation reservation);

    boolean existsActiveReservationBySeat(Seat seat);
}
