package kr.hhplus.be.server.concert.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;

import java.util.List;
import java.util.UUID;

@Schema(description = "콘서트 예약 랭킹 응답")
public record ConcertRankingResponse(
        @Schema(description = "랭킹 목록")
        List<Item> rankings
) {
    public static ConcertRankingResponse from(List<ConcertRankingItem> items) {
        return new ConcertRankingResponse(
                items.stream()
                        .map(i -> new Item(i.concertId(), i.reservationCount()))
                        .toList()
        );
    }

    @Schema(description = "콘서트 예약 랭킹 항목")
    public record Item(
            @Schema(description = "콘서트 ID")
            UUID concertId,
            @Schema(description = "예약 수", example = "100")
            long reservationCount
    ) {
    }
}
