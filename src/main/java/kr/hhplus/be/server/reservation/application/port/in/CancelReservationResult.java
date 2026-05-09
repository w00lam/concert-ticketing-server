package kr.hhplus.be.server.reservation.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Carries the result returned by the reservation use case.
 */

public record CancelReservationResult(UUID reservationId, String status, LocalDateTime canceledAt) {
}
