package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.GetConcertRankingService;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

public class ConcertRankingServiceTest extends BaseUnitTest {
    @Mock
    ConcertRankingRepositoryPort concertRankingRepository;

    @InjectMocks
    GetConcertRankingService getConcertRankingService;


    @Test
    void 예약이_확정되면_콘서트_랭킹이_증가한다() {
        // when
        getConcertRankingService.increaseReservation(fixedUUID());

        // then
        verify(concertRankingRepository).increase(fixedUUID(), 1L);
    }


    @Test
    void 예약이_취소되면_콘서트_랭킹이_감소한다() {
        // when
        getConcertRankingService.decreaseReservation(fixedUUID());

        // then
        verify(concertRankingRepository).decrease(fixedUUID(), 1L);
    }
}
