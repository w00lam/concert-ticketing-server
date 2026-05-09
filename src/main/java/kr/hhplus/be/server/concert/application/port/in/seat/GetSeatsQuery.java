package kr.hhplus.be.server.concert.application.port.in.seat;

import java.util.UUID;
/**
 * Carries query conditions required by the concert use case.
 */

public record GetSeatsQuery(UUID concertDateId) {
}
