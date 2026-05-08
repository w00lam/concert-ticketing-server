package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.ConcertRankingService;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

public class ConcertRankingServiceTest extends BaseUnitTest {
    @Mock
    ConcertRankingRepositoryPort concertRankingRepository;

    @InjectMocks
    ConcertRankingService concertRankingService;

    @Test
    void 예약이_확정되면_콘서트_랭킹을_증가시킨다() {
        concertRankingService.increaseReservation(fixedUUID());

        verify(concertRankingRepository).increase(fixedUUID(), 1L);
    }

    @Test
    void 예약이_취소되면_콘서트_랭킹을_감소시킨다() {
        concertRankingService.decreaseReservation(fixedUUID());

        verify(concertRankingRepository).decrease(fixedUUID(), 1L);
    }
}
