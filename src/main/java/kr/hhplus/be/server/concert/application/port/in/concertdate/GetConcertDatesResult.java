package kr.hhplus.be.server.concert.application.port.in.concertdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
/**
 * Carries the result returned by the concert use case.
 */

public record GetConcertDatesResult(UUID concertDateId, LocalDate eventDate) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
