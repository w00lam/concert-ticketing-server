package kr.hhplus.be.server.integration.performance;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesUseCase;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import kr.hhplus.be.server.TestcontainersConfiguration;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersConfiguration.Initializer.class)
public class ConcertAndSeatPerformanceTest {

    @Autowired
    private GetConcertDatesUseCase getConcertDatesUseCase;

    @Autowired
    private GetSeatsUseCase getSeatsUseCase;

    private UUID testConcertId;
    private UUID testConcertDateId;

    @BeforeEach
    void setUp() {
        // 테스트용 콘서트 ID 설정
        testConcertId = UUID.fromString("3830d228-5a82-4088-b402-674f0df45ada");
        testConcertDateId =UUID.fromString("C9021C25-810E-409A-AFA0-9A3C28C8139E");
    }

    @Test
    @DisplayName("캐시 적용 전 - 콘서트 목록 조회 성능 측정")
    void testGetConcertDatesPerformanceBeforeCache() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("[캐시 적용 전] 총 100회 콘서트 목록 조회 소요 시간(ms): " + duration);

        var result = getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
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
        System.out.println("[캐시 적용 후] 총 100회 콘서트 목록 조회 소요 시간(ms): " + duration);

        var result = getConcertDatesUseCase.execute(new GetConcertDatesQuery(testConcertId));
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
        System.out.println("[캐시 적용 전] 총 100회 좌석 조회 소요 시간(ms): " + duration);

        var seats = getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
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
        System.out.println("[캐시 적용 후] 총 100회 좌석 조회 소요 시간(ms): " + duration);

        var seats = getSeatsUseCase.execute(new GetSeatsQuery(testConcertDateId));
        assertThat(seats).isNotEmpty();
    }
}
