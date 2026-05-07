package kr.hhplus.be.server.unit.application.concert.service;

import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.application.service.SeatHoldReleaseScheduler;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SeatHoldReleaseSchedulerTest {
    @Mock
    private SeatRepositoryPort seatRepository;

    private SeatHoldReleaseScheduler scheduler;

    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalDateTime now = LocalDateTime.of(2035, 12, 12, 12, 12);
        ZoneId zone = ZoneId.systemDefault();
        Clock clock = Clock.fixed(now.atZone(zone).toInstant(), zone);
        scheduler = new SeatHoldReleaseScheduler(seatRepository, clock);
        seat1 = Seat.builder().held(true).holdUntil(now.minusMinutes(1)).deleted(false).build();
        seat2 = Seat.builder().held(true).holdUntil(now.minusMinutes(1)).deleted(false).build();
    }

    @Test
    void releaseExpiredHolds_clearsHoldState() {
        List<Seat> expiredSeats = List.of(seat1, seat2);
        when(seatRepository.findSeatsByConcertDateIdForHoldRelease(any(LocalDateTime.class)))
                .thenReturn(expiredSeats);

        scheduler.releaseExpiredHolds();

        // The scheduler persists each released seat after clearing its temporary hold state.
        verify(seatRepository, times(1)).save(seat1);
        verify(seatRepository, times(1)).save(seat2);
        assertFalse(seat1.isHeld());
        assertFalse(seat2.isHeld());
        assertNull(seat1.getHoldUntil());
        assertNull(seat2.getHoldUntil());
    }
}
