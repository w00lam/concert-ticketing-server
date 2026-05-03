package kr.hhplus.be.server.concert.application.port.in.concertdate;

import java.util.UUID;

public record GetConcertDatesQuery(UUID concertId) {
}