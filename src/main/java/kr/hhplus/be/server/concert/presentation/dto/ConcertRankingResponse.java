package kr.hhplus.be.server.concert.presentation.dto;

import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;

import java.util.List;
import java.util.UUID;

public record ConcertRankingResponse(List<Item> rankings) {
    public static ConcertRankingResponse from(List<ConcertRankingItem> items) {
        return new ConcertRankingResponse(
                items.stream()
                        .map(i -> new Item(i.concertId(), i.reservationCount()))
                        .toList()
        );
    }

    public record Item(UUID concertId, long reservationCount) {
    }
}
