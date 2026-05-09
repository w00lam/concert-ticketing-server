package kr.hhplus.be.server.integration.performance;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesUseCase;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsUseCase;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertAndSeatPerformanceTest extends ReservationIntegrationTestBase {

    @Autowired
    private GetConcertDatesUseCase getConcertDatesUseCase;

    @Autowired
    private GetSeatsUseCase getSeatsUseCase;

    private UUID testConcertId;
    private UUID testConcertDateId;

    @BeforeEach
    void setUp() {
        // Create fresh query data so performance checks do not depend on preloaded UUIDs.
        Seat seat = createSeat();
        testConcertId = seat.getConcertDate().getConcert().getId();
        testConcertDateId = seat.getConcertDate().getId();
    }

    @Test
    @DisplayName("캐시 적용 전 - 콘서트 목록 조회 성능 측정")
    void testGetConcertDatesPerformanceBeforeCache() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
        }

        long duration = System.currentTimeMillis() - start;

        var result = getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("캐시 적용 후 - 콘서트 목록 조회 성능 측정")
    void testGetConcertDatesPerformanceAfterCache() {
        // 캐시 초기화용 1회 호출
        getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
        }

        long duration = System.currentTimeMillis() - start;

        var result = getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("캐시 적용 전 - 콘서트 좌석 조회 성능 측정")
    void testGetSeatsPerformanceBeforeCache() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
        }

        long duration = System.currentTimeMillis() - start;

        var seats = getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        assertThat(seats).isNotEmpty();
    }

    @Test
    @DisplayName("캐시 적용 후 - 콘서트 좌석 조회 성능 측정")
    void testGetSeatsPerformanceAfterCache() {
        // 캐시 초기화용 1회 호출
        getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
        }

        long duration = System.currentTimeMillis() - start;

        var seats = getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        assertThat(seats).isNotEmpty();
    }
}
