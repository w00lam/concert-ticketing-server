package kr.hhplus.be.server.reservation.presentation.dto;

import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConfirmReservationResponse(UUID reservationId, String status, LocalDateTime confirmedAt) {
    public static ConfirmReservationResponse from(ConfirmReservationResult result) {
        return new ConfirmReservationResponse(
                result.reservationId(),
                result.status(),
                result.confirmedAt()
        );
    }
}