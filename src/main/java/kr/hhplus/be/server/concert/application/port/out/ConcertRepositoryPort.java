package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.model.Concert;

public interface ConcertRepositoryPort {
    Concert save(Concert concert);
}
