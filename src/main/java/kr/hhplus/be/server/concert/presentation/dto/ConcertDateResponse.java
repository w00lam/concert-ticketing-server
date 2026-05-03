package kr.hhplus.be.server.concert.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;

import java.time.LocalDate;
import java.util.UUID;
/**
 * Carries concert API response values.
 */

@Schema(description = "콘서트 날짜 응답")
public record ConcertDateResponse(
        @Schema(description = "콘서트 날짜 ID")
        UUID id,
        @Schema(description = "공연 날짜", example = "2026-05-02")
        LocalDate eventDate
) {
    public static ConcertDateResponse from(GetConcertDatesResult result) {
        return new ConcertDateResponse(
                result.concertDateId(),
                result.eventDate()
        );
    }
}
