package kr.hhplus.be.server.reservation.application.event;

import java.util.UUID;

public record ReservationConfirmedEvent(UUID reservationId, UUID concertId) {
}
