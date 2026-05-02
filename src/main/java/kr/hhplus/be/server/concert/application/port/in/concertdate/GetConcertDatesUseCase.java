package kr.hhplus.be.server.concert.application.port.in.concertdate;

import java.util.List;

public interface GetConcertDatesUseCase {
    List<GetConcertDatesResult> execute(GetConcertDatesQuery query);
}
