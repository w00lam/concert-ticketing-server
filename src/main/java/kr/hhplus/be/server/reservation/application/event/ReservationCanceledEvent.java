package kr.hhplus.be.server.reservation.application.event;

import java.util.UUID;
/**
 * Handles events raised by the reservation flow.
 */

public record ReservationCanceledEvent(UUID reservationId, UUID concertId) {
}
