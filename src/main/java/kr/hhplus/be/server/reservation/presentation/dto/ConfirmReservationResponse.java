package kr.hhplus.be.server.reservation.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "예약 확정 응답")
public record ConfirmReservationResponse(
        @Schema(description = "예약 ID")
        UUID reservationId,
        @Schema(description = "예약 상태", example = "CONFIRMED")
        String status,
        @Schema(description = "예약 확정 시각")
        LocalDateTime confirmedAt
) {
    public static ConfirmReservationResponse from(ConfirmReservationResult result) {
        return new ConfirmReservationResponse(
                result.reservationId(),
                result.status(),
                result.confirmedAt()
        );
    }
}
