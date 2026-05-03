package kr.hhplus.be.server.concert.application.service;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
/**
 * Provides application service behavior for the concert feature.
 */


@Service
@RequiredArgsConstructor
public class GetConcertRankingService {
    private final ConcertRankingRepositoryPort repository;

    public List<ConcertRankingItem> getTopRankings(int limit) {
        return repository.findTopRanked(limit);
    }

    public void increaseReservation(UUID concertId) {
        repository.increase(concertId, 1L);
    }

    public void decreaseReservation(UUID concertId) {
        repository.decrease(concertId, 1L);
    }
}
