package kr.hhplus.be.server.reservation.application.port.out;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;

import java.util.UUID;

public interface ReservationRepositoryPort {
    Reservation findById(UUID reservationId);

    boolean confirmIfNotExpired(UUID reservationId);

    Reservation save(Reservation reservation);

    boolean existsBySeatAndStatus(Seat seat, ReservationStatus status);

    boolean existsActiveReservationBySeat(Seat seat);

    long countBySeatAndStatus(Seat seat, ReservationStatus status);
}
