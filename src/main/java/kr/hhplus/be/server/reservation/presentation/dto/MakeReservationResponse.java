package kr.hhplus.be.server.reservation.presentation.dto;

import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record MakeReservationResponse(UUID reservationId, UUID userId, UUID seatId, String status,
                                      LocalDateTime tempHoldExpiresAt) {
    public static MakeReservationResponse from(MakeReservationResult result) {
        return new MakeReservationResponse(
                result.reservationId(),
                result.userId(),
                result.seatId(),
                result.status(),
                result.tempHoldExpiresAt());
    }
}
