package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.concert.domain.model.seat.SeatLockKey;
import kr.hhplus.be.server.common.application.lock.DistributedLockManager;
import kr.hhplus.be.server.reservation.domain.model.ReservationExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
/**
 * Implements the reservation use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class MakeReservationUseCaseImpl implements MakeReservationUseCase {
    private final ReservationCreationService reservationCreationService;
    private final DistributedLockManager lockManager;


    @Override
    public MakeReservationResult execute(MakeReservationCommand command) {
        // Serialize attempts for the same concert seat before entering the DB transaction.
        String lockKey = SeatLockKey.of(command.concertId(), command.seatId());
        String lockValue = lockManager.lock(lockKey, Duration.ofSeconds(5));


        if (lockValue == null) {
            throw ReservationExceptions.seatAlreadyReserved();
        }


        try {
            return reservationCreationService.create(command);
        } finally {
            // Unlock with the owner token so another request's lock is not released accidentally.
            lockManager.unlock(lockKey, lockValue);
        }
    }
}
