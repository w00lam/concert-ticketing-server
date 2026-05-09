package kr.hhplus.be.server.unit.domain.concert.model;

import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ConcertTest extends BaseUnitTest {
    @Test
    @DisplayName("Concert 객체 생성 시 Builder 필드 검증")
    void testConcertBuilderInitialization() {
        // when
        Concert concert = Concert.create("Test Concert", "Test Description");

        // then
        assertNull(concert.getId());
        assertEquals("Test Concert", concert.getTitle());
        assertEquals("Test Description", concert.getDescription());

        // 연관 엔티티 초기값 확인
        assertNotNull(concert.getConcertDates(), "concertDates는 초기화되어 있어야 한다");
        assertTrue(concert.getConcertDates().isEmpty(), "초기 concertDates 리스트는 비어 있어야 한다");

        // JPA Auditing 필드 확인 (단위 테스트에서는 null 가능)
        assertNull(concert.getCreatedAt(), "단위 테스트 시 createdAt은 null 가능");
        assertNull(concert.getUpdatedAt(), "단위 테스트 시 updatedAt은 null 가능");
    }

    @Test
    @DisplayName("Concert에 ConcertDate 추가/삭제 기능 검증")
    void testAddAndRemoveConcertDate() {
        // given
        Concert concert = Concert.create("Test Concert", "Test Description");

        ConcertDate concertDate = ConcertDate.create(concert, LocalDate.now());

        // when: add
        concert.addDate(concertDate);

        // then
        assertEquals(1, concert.getConcertDates().size());
        assertEquals(concert, concertDate.getConcert());

        // when: remove
        concert.removeDate(concertDate);

        // then
        assertTrue(concert.getConcertDates().isEmpty());
        assertNull(concertDate.getConcert());
    }
}
