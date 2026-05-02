package kr.hhplus.be.server.reservation.infrastructure.persistence;

import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.reservation.infrastructure.persistence.JpaReservationRepository;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepositoryPort {
    private final JpaReservationRepository jpa;

    @Override
    public Reservation findById(UUID reservationId) {
        return jpa.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
    }

    @Override
    public boolean confirmIfNotExpired(UUID reservationId) {
        return jpa.confirmIfNotExpired(reservationId) == 1;
    }

    @Override
    public Reservation save(Reservation reservation) {
        return jpa.save(reservation);
    }

    @Override
    public boolean existsBySeatAndStatus(Seat seat, ReservationStatus status) {
        return jpa.existsBySeatAndStatus(seat, status);
    }

    @Override
    public boolean existsActiveReservationBySeat(Seat seat) {
        return jpa.existsActiveReservationBySeat(seat);
    }

    @Override
    public long countBySeatAndStatus(Seat seat, ReservationStatus status) {
        return jpa.countBySeatAndStatus(seat, status);
    }
}
