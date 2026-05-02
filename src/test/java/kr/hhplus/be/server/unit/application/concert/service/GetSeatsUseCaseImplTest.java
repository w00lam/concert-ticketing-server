package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsQuery;
import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;
import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.application.service.GetSeatsUseCaseImpl;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;
import org.mockito.*;

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
    @DisplayName("콘서트 날짜 ID로 좌석 목록 조회 시 Result 리스트로 변환된다")
    void execute_success() {
        // given
        UUID concertDateId = fixedUUID();
        GetSeatsQuery query = new GetSeatsQuery(concertDateId);

        Seat seat1 = Seat.builder()
                .id(fixedUUID())
                .section("A")
                .row("1")
                .number("10")
                .grade("VIP")
                .build();

        Seat seat2 = Seat.builder()
                .id(fixedUUID2())
                .section("B")
                .row("2")
                .number("20")
                .grade("R")
                .build();

        when(seatRepositoryPort.findSeatsByConcertDateId(concertDateId)).thenReturn(List.of(seat1, seat2));

        // when
        List<GetSeatsResult> results = useCase.execute(query);

        // then
        assertEquals(2, results.size());

        assertEquals(seat1.getId(), results.get(0).seatId());
        assertEquals(seat1.getSection(), results.get(0).section());
        assertEquals(seat1.getRow(), results.get(0).row());
        assertEquals(seat1.getNumber(), results.get(0).number());
        assertEquals(seat1.getGrade(), results.get(0).grade());

        assertEquals(seat2.getId(), results.get(1).seatId());
        assertEquals(seat2.getSection(), results.get(1).section());
        assertEquals(seat2.getRow(), results.get(1).row());
        assertEquals(seat2.getNumber(), results.get(1).number());
        assertEquals(seat2.getGrade(), results.get(1).grade());
    }
}
