package kr.hhplus.be.server.reservation.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
/**
 * Carries reservation API request values.
 */

@Schema(description = "좌석 예약 요청")
public record MakeReservationRequest(
        @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID userId,
        @Schema(description = "콘서트 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID concertId,
        @Schema(description = "좌석 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID seatId
) {
}
