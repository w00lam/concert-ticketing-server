package kr.hhplus.be.server.reservation.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "좌석 예약 응답")
public record MakeReservationResponse(
        @Schema(description = "예약 ID")
        UUID reservationId,
        @Schema(description = "사용자 ID")
        UUID userId,
        @Schema(description = "좌석 ID")
        UUID seatId,
        @Schema(description = "예약 상태", example = "TEMP_HOLD")
        String status,
        @Schema(description = "임시 선점 만료 시각")
        LocalDateTime tempHoldExpiresAt
) {
    public static MakeReservationResponse from(MakeReservationResult result) {
        return new MakeReservationResponse(
                result.reservationId(),
                result.userId(),
                result.seatId(),
                result.status(),
                result.tempHoldExpiresAt());
    }
}
