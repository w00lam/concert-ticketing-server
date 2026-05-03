package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.service.MakeReservationUseCaseImpl;
import kr.hhplus.be.server.reservation.application.service.ReservationTxService;
import kr.hhplus.be.server.concert.domain.model.seat.SeatLockKey;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.common.application.lock.DistributedLockManager;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MakeReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationTxService reservationTxService;

    @Mock
    DistributedLockManager lockManager;

    @InjectMocks
    MakeReservationUseCaseImpl useCase;

    @Test
    @DisplayName("Redis lock is released after successful temporary reservation")
    void execute_success() {
        UUID userId = fixedUUID();
        UUID concertId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        String lockKey = SeatLockKey.of(concertId, seatId);
        String lockValue = "lock-owner";
        LocalDateTime expiresAt = fixedNow().plusMinutes(5);

        MakeReservationCommand command = new MakeReservationCommand(userId, concertId, seatId);
        MakeReservationResult expected = new MakeReservationResult(
                UUID.randomUUID(),
                userId,
                seatId,
                ReservationStatus.TEMP_HOLD.name(),
                expiresAt
        );

        when(lockManager.lock(lockKey, Duration.ofSeconds(5))).thenReturn(lockValue);
        when(reservationTxService.reserve(command)).thenReturn(expected);

        MakeReservationResult result = useCase.execute(command);

        assertEquals(expected, result);

        // The lock must be released with the exact owner token acquired for this request.
        verify(lockManager).unlock(lockKey, lockValue);
    }
}
