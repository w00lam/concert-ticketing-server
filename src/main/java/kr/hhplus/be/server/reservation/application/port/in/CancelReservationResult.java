package kr.hhplus.be.server.reservation.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record CancelReservationResult(UUID reservationId, String status, LocalDateTime canceledAt) {
}
