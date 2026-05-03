package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Implements the reservation use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class ConfirmReservationUseCaseImpl implements ConfirmReservationUseCase {
    private final ReservationRepositoryPort reservationRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public ConfirmReservationResult execute(ConfirmReservationCommand command) {
        boolean success = reservationRepository.confirmIfNotExpired(command.reservationId());

        if (!success) {
            throw new BusinessRuleViolationException(ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED, "예약이 만료되었거나 이미 처리되었습니다.");
        }

        var reservation = reservationRepository.findById(command.reservationId());

        eventPublisher.publish(
                new ReservationConfirmedEvent(
                        reservation.getId(),
                        reservation.getSeat().getConcertDate().getConcert().getId()
                )
        );

        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getConfirmedAt()
        );
    }
}
