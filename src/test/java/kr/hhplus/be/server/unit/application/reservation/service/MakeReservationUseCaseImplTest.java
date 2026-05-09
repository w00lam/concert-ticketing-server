package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.service.MakeReservationUseCaseImpl;
import kr.hhplus.be.server.reservation.application.service.ReservationCreationService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MakeReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationCreationService reservationCreationService;

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
        when(reservationCreationService.create(command)).thenReturn(expected);

        MakeReservationResult result = useCase.execute(command);

        assertEquals(expected, result);

        // The lock must be released with the exact owner token acquired for this request.
        verify(lockManager).unlock(lockKey, lockValue);
    }

    @Test
    @DisplayName("Redis lock acquisition failure is exposed as a structured business exception")
    void execute_lockFailed() {
        UUID userId = fixedUUID();
        UUID concertId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        String lockKey = SeatLockKey.of(concertId, seatId);

        MakeReservationCommand command = new MakeReservationCommand(userId, concertId, seatId);

        when(lockManager.lock(lockKey, Duration.ofSeconds(5))).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOfSatisfying(BusinessRuleViolationException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.SEAT_ALREADY_RESERVED)
                )
                .hasMessage("이미 예약 중인 좌석입니다.");

        verify(reservationCreationService, never()).create(command);
    }
}
