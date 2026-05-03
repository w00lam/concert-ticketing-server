package kr.hhplus.be.server.reservation.application.port.in;

import java.time.LocalDateTime;
import java.util.UUID;

public record MakeReservationResult(UUID reservationId, UUID userId, UUID seatId, String status,
                                    LocalDateTime tempHoldExpiresAt) {
}
