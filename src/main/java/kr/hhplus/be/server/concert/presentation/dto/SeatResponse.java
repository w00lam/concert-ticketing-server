package kr.hhplus.be.server.concert.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;

import java.util.UUID;
/**
 * Carries concert API response values.
 */

@Schema(description = "좌석 응답")
public record SeatResponse(
        @Schema(description = "좌석 ID")
        UUID seatId,
        @Schema(description = "구역", example = "A")
        String section,
        @Schema(description = "열", example = "1")
        String row,
        @Schema(description = "좌석 번호", example = "12")
        String number,
        @Schema(description = "좌석 등급", example = "VIP")
        String grade
) {
    public static SeatResponse from(GetSeatsResult result) {
        return new SeatResponse(
                result.seatId(),
                result.section(),
                result.row(),
                result.number(),
                result.grade()
        );
    }
}
