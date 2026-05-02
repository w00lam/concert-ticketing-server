package kr.hhplus.be.server.concert.presentation.dto;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;

import java.time.LocalDate;
import java.util.UUID;

public record ConcertDateResponse(UUID id, LocalDate eventDate) {
    public static ConcertDateResponse from(GetConcertDatesResult result) {
        return new ConcertDateResponse(
                result.concertDateId(),
                result.eventDate()
        );
    }
}