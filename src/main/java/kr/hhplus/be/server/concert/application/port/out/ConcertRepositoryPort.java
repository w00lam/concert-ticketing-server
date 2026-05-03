package kr.hhplus.be.server.concert.application.port.out;

import kr.hhplus.be.server.concert.domain.model.Concert;
/**
 * Defines the output port for concert persistence.
 */

public interface ConcertRepositoryPort {
    Concert save(Concert concert);
}
