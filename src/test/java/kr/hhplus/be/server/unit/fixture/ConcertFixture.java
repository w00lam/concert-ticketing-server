package kr.hhplus.be.server.unit.fixture;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;

import java.time.LocalDate;
import java.util.UUID;

public final class ConcertFixture {
    private ConcertFixture() {
    }

    public static Concert concert(UUID id) {
        return Concert.builder()
                .id(id)
                .title("Test Concert")
                .description("Test Description")
                .build();
    }

    public static ConcertDate concertDate(Concert concert) {
        return concertDate(concert, LocalDate.of(2030, 1, 1));
    }

    public static ConcertDate concertDate(Concert concert, LocalDate eventDate) {
        return ConcertDate.create(concert, eventDate);
    }

    public static ConcertDate concertDate(UUID id, LocalDate eventDate) {
        return ConcertDate.builder()
                .id(id)
                .eventDate(eventDate)
                .build();
    }

    public static Seat seat(ConcertDate concertDate) {
        return Seat.builder()
                .concertDate(concertDate)
                .build();
    }

    public static Seat seat(UUID id, ConcertDate concertDate, String section, String row, String number, String grade) {
        return Seat.builder()
                .id(id)
                .concertDate(concertDate)
                .section(section)
                .row(row)
                .number(number)
                .grade(grade)
                .deleted(false)
                .build();
    }
}
