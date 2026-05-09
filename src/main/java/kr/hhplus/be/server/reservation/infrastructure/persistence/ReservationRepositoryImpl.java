package kr.hhplus.be.server.reservation.infrastructure.persistence;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Implements the reservation repository output port with JPA.
 */

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryPort {
    private final JpaReservationRepository jpa;

    @Override
    public Reservation findById(UUID reservationId) {
        return jpa.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESERVATION_NOT_FOUND, "예약을 찾을 수 없습니다: " + reservationId));
    }

    @Override
    public boolean confirmIfNotExpired(UUID reservationId, LocalDateTime now) {
        return jpa.confirmIfNotExpired(reservationId, now) == 1;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }

    @Override
    public boolean existsActiveReservationBySeat(Seat seat, LocalDateTime now) {
        return jpa.existsActiveReservationBySeat(seat, now);
    }
}
