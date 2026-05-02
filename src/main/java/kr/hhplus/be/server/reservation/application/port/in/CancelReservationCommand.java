package kr.hhplus.be.server.reservation.application.port.in;

import java.util.UUID;

public record CancelReservationCommand(UUID reservationId) {
}
