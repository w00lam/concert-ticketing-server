package kr.hhplus.be.server.reservation.application.port.in;

import java.util.UUID;

public record MakeReservationCommand(UUID userId, UUID concertId, UUID seatId) {
}
