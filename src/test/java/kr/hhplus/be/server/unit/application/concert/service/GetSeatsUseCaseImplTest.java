package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;
import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.application.service.GetSeatsUseCaseImpl;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class GetSeatsUseCaseImplTest extends BaseUnitTest {
    @Mock
    SeatRepositoryPort seatRepositoryPort;

    @InjectMocks
    GetSeatsUseCaseImpl useCase;

    @Test
    @DisplayName("콘서트 날짜 ID로 좌석 목록 조회 시 Result 리스트를 반환한다")
    void execute_success() {
        UUID concertDateId = fixedUUID();
        GetSeatsQuery query = new GetSeatsQuery(concertDateId);
        GetSeatsResult seat1 = new GetSeatsResult(
                fixedUUID(),
                "A",
                "1",
                "10",
                "VIP"
        );
        GetSeatsResult seat2 = new GetSeatsResult(
                fixedUUID2(),
                "B",
                "2",
                "20",
                "R"
        );

        when(seatRepositoryPort.findSeatResultsByConcertDateId(concertDateId)).thenReturn(List.of(seat1, seat2));

        List<GetSeatsResult> results = useCase.execute(query);

        assertEquals(2, results.size());
        assertEquals(seat1.seatId(), results.get(0).seatId());
        assertEquals(seat1.section(), results.get(0).section());
        assertEquals(seat1.row(), results.get(0).row());
        assertEquals(seat1.number(), results.get(0).number());
        assertEquals(seat1.grade(), results.get(0).grade());
        assertEquals(seat2.seatId(), results.get(1).seatId());
        assertEquals(seat2.section(), results.get(1).section());
        assertEquals(seat2.row(), results.get(1).row());
        assertEquals(seat2.number(), results.get(1).number());
        assertEquals(seat2.grade(), results.get(1).grade());
    }
}
