package kr.hhplus.be.server.reservation.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmReservationResult(UUID reservationId, String status, LocalDateTime confirmedAt) {
}
