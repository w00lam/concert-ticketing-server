package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.out.ConcertDateRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
/**
 * Implements the concert repository output port with JPA.
 */

@Repository
@RequiredArgsConstructor
public class ConcertDateRepositoryImpl implements ConcertDateRepositoryPort {
    private final JpaConcertDateRepository jpa;


    @Override
    public ConcertDate save(ConcertDate concertDate) {
        return jpa.save(concertDate);
    }

    @Override
    public List<ConcertDate> findDatesByConcertId(UUID concertId) {
        return jpa.findAllByConcert_Id(concertId);
    }
}
