package kr.hhplus.be.server.concert.application.port.in.seat;

import java.util.UUID;

public record GetSeatsQuery(UUID concertDateId) {
}
