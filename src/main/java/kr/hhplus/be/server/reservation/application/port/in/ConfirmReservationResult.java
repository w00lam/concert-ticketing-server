package kr.hhplus.be.server.reservation.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Carries the result returned by the reservation use case.
 */

public record ConfirmReservationResult(UUID reservationId, String status, LocalDateTime confirmedAt) {
}
