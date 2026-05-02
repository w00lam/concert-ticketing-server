package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.out.ConcertRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.infrastructure.persistence.JpaConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepositoryPort {
    private final JpaConcertRepository jpa;

    @Override
    public Concert save(Concert concert) {
        return jpa.save(concert);
    }
}
