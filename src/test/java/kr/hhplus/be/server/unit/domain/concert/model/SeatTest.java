package kr.hhplus.be.server.unit.domain.concert.model;

import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SeatTest extends BaseUnitTest {
    @Test
    @DisplayName("Seat 객체 생성 시 필수 필드와 기본값이 올바르게 초기화되어야 한다")
    void testSeatBuilderInitialization() {
        // given
        Concert concert = Concert.create("Test Concert", "Test Description");

        ConcertDate concertDate = ConcertDate.builder()
                .id(fixedUUID())
                .concert(concert)
                .eventDate(LocalDate.of(2025, 12, 25))
                .deleted(false)
                .build();

        // when
        Seat seat = Seat.create(concertDate, "A", "1", "01", "VIP");

        // then
        assertNull(seat.getId(), "id는 JPA가 영속화 시점에 생성한다");
        assertEquals(concertDate, seat.getConcertDate(), "ConcertDate 연관관계가 올바르게 설정되어야 한다");
        assertEquals("A", seat.getSection());
        assertEquals("1", seat.getRow());
        assertEquals("01", seat.getNumber());
        assertEquals("VIP", seat.getGrade());
        assertFalse(seat.isDeleted(), "deleted 기본값은 false여야 한다");

        // Auditing 필드는 단위 테스트 시 null 허용
        assertNull(seat.getCreatedAt(), "createdAt은 JPA에서 설정되므로 테스트 시 null 가능");
        assertNull(seat.getUpdatedAt(), "updatedAt은 JPA에서 설정되므로 테스트 시 null 가능");
    }
}
