package kr.hhplus.be.server.unit.fixture;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ReservationFixture {
    private ReservationFixture() {
    }

    public static Reservation reservation(UUID id) {
        return Reservation.builder()
                .id(id)
                .build();
    }

    public static Reservation reservation(UUID id, User user) {
        return Reservation.builder()
                .id(id)
                .user(user)
                .build();
    }

    public static Reservation reservation(UUID id, Seat seat, ReservationStatus status) {
        return Reservation.builder()
                .id(id)
                .seat(seat)
                .status(status)
                .build();
    }

    public static Reservation confirmed(UUID id, Seat seat, LocalDateTime confirmedAt) {
        return Reservation.builder()
                .id(id)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .confirmedAt(confirmedAt)
                .build();
    }

    public static Reservation tempHold(UUID id) {
        return Reservation.builder()
                .id(id)
                .status(ReservationStatus.TEMP_HOLD)
                .build();
    }
}
