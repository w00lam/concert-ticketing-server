package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.model.ConcertDate;

import java.util.List;
import java.util.UUID;

public interface ConcertDateRepositoryPort {
    ConcertDate save(ConcertDate concertDate);
    List<ConcertDate> findDatesByConcertId(UUID concertId);
}
