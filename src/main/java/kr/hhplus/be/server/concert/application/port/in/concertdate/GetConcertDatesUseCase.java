package kr.hhplus.be.server.concert.application.port.in.concertdate;

import java.util.List;
/**
 * Defines the input port for the concert use case.
 */

public interface GetConcertDatesUseCase {
    List<GetConcertDatesResult> execute(GetConcertDatesQuery query);
}
