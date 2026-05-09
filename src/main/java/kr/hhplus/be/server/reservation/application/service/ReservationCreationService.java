package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationExceptions;
import kr.hhplus.be.server.reservation.domain.model.ReservationExpirationPolicy;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Creates temporary reservations inside a transaction.
 */

@Service
@RequiredArgsConstructor
public class ReservationCreationService {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SeatRepositoryPort seatRepositoryPort;
    private final ReservationExpirationPolicy policy;
    private final Clock clock;

    @Transactional
    public MakeReservationResult create(MakeReservationCommand command) {
        LocalDateTime now = LocalDateTime.now(clock);
        User user = userRepositoryPort.findById(command.userId());
        Seat seat = seatRepositoryPort.findById(command.seatId());

        // Active holds are the only records that should block a new temporary reservation.
        if (reservationRepositoryPort.existsActiveReservationBySeat(seat, now)) {
            throw ReservationExceptions.seatAlreadyReserved();
        }

        Reservation tempHold = Reservation.create(user, seat, clock, policy);
        reservationRepositoryPort.save(tempHold);

        return new MakeReservationResult(
                tempHold.getId(),
                user.getId(),
                seat.getId(),
                tempHold.getStatus().name(),
                tempHold.getTempHoldExpiresAt()
        );
    }
}
