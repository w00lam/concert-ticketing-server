package kr.hhplus.be.server.reservation.application.event;

import java.util.UUID;

public record ReservationCanceledEvent(UUID reservationId, UUID concertId) {
}
