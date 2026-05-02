package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesQuery;
import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;
import kr.hhplus.be.server.concert.application.port.out.ConcertDateRepositoryPort;
import kr.hhplus.be.server.concert.application.service.GetConcertDatesUseCaseImpl;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;
import org.mockito.*;

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
    @DisplayName("공연 ID로 날짜 목록 조회 시 Result 리스트로 변환된다")
    void execute_success() {
        // given
        UUID concertId = fixedUUID();
        GetConcertDatesQuery query = new GetConcertDatesQuery(concertId);

        ConcertDate date1 = ConcertDate.builder()
                .id(fixedUUID())
                .eventDate(LocalDate.of(2030, 1, 1))
                .build();

        ConcertDate date2 = ConcertDate.builder()
                .id(fixedUUID2())
                .eventDate(LocalDate.of(2030, 1, 2))
                .build();

        when(concertDateRepositoryPort.findDatesByConcertId(concertId)).thenReturn(List.of(date1, date2));

        // when
        List<GetConcertDatesResult> results = useCase.execute(query);

        // then
        assertEquals(2, results.size());
        assertEquals(date1.getId(), results.get(0).concertDateId());
        assertEquals(date1.getEventDate(), results.get(0).eventDate());

        assertEquals(date2.getId(), results.get(1).concertDateId());
        assertEquals(date2.getEventDate(), results.get(1).eventDate());
    }
}
