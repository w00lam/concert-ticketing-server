package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Implements the reservation use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class ConfirmReservationUseCaseImpl implements ConfirmReservationUseCase {
    private final ReservationConfirmationService reservationConfirmationService;

    @Override
    @Transactional
    public ConfirmReservationResult execute(ConfirmReservationCommand command) {
        Reservation reservation = reservationConfirmationService.confirm(command.reservationId());

        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getConfirmedAt()
        );
    }
}
