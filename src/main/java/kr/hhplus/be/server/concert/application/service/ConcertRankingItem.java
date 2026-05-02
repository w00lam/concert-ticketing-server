package kr.hhplus.be.server.concert.application.service;

import java.util.UUID;

public record ConcertRankingItem(UUID concertId, long reservationCount) {
}
