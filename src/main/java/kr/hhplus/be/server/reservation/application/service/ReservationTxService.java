package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.concert.application.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationExpirationPolicy;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
/**
 * Provides application service behavior for the reservation feature.
 */

@Service
@RequiredArgsConstructor
public class ReservationTxService {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SeatRepositoryPort seatRepositoryPort;
    private final ReservationExpirationPolicy policy;
    private final Clock clock;


    @Transactional
    public MakeReservationResult reserve(MakeReservationCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        Seat seat = seatRepositoryPort.findById(command.seatId());


        // Active holds are the only records that should block a new temporary reservation.
        if (reservationRepositoryPort.existsActiveReservationBySeat(seat)) {
            throw new BusinessRuleViolationException(ErrorCode.SEAT_ALREADY_RESERVED, "이미 예약 중인 좌석입니다.");
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
