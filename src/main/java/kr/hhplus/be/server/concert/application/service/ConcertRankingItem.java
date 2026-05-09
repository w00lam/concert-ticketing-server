package kr.hhplus.be.server.concert.application.service;

import java.util.UUID;
/**
 * Represents a read model used by the concert application service.
 */

public record ConcertRankingItem(UUID concertId, long reservationCount) {
}
