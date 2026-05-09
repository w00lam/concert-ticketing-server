package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;
import kr.hhplus.be.server.concert.application.port.out.ConcertDateRepositoryPort;
import kr.hhplus.be.server.concert.application.service.GetConcertDatesUseCaseImpl;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GetConcertDatesUseCaseImplTest extends BaseUnitTest {
    @Mock
    ConcertDateRepositoryPort concertDateRepositoryPort;

    @InjectMocks
    GetConcertDatesUseCaseImpl useCase;

    @Test
    @DisplayName("공연 ID로 날짜 목록 조회 시 Result 리스트를 반환한다")
    void execute_success() {
        UUID concertId = fixedUUID();
        GetConcertDatesQuery query = new GetConcertDatesQuery(concertId);
        GetConcertDatesResult date1 = new GetConcertDatesResult(
                fixedUUID(),
                LocalDate.of(2030, 1, 1)
        );
        GetConcertDatesResult date2 = new GetConcertDatesResult(
                fixedUUID2(),
                LocalDate.of(2030, 1, 2)
        );

        when(concertDateRepositoryPort.findDateResultsByConcertId(concertId)).thenReturn(List.of(date1, date2));

        List<GetConcertDatesResult> results = useCase.execute(query);

        assertEquals(2, results.size());
        assertEquals(date1.concertDateId(), results.get(0).concertDateId());
        assertEquals(date1.eventDate(), results.get(0).eventDate());
        assertEquals(date2.concertDateId(), results.get(1).concertDateId());
        assertEquals(date2.eventDate(), results.get(1).eventDate());
    }
}
