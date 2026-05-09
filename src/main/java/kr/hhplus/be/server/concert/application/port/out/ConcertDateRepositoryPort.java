package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;

import java.util.List;
import java.util.UUID;
/**
 * Defines the output port for concert persistence.
 */

public interface ConcertDateRepositoryPort {
    ConcertDate save(ConcertDate concertDate);
    List<GetConcertDatesResult> findDateResultsByConcertId(UUID concertId);
}
