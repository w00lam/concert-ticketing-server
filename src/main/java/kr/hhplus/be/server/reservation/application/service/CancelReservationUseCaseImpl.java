package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.event.ReservationCanceledEvent;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationUseCase;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Implements the reservation use case and coordinates transactional work.
 */


@Service
@RequiredArgsConstructor
public class CancelReservationUseCaseImpl implements CancelReservationUseCase {
    private final ReservationRepositoryPort reservationRepository;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public CancelReservationResult execute(CancelReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.reservationId());

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new BusinessRuleViolationException(ErrorCode.RESERVATION_ALREADY_CANCELLED, "이미 취소된 예약입니다.");
        }

        boolean wasConfirmed = reservation.getStatus() == ReservationStatus.CONFIRMED;

        reservation.cancel();
        reservationRepository.save(reservation);

        if (wasConfirmed) {
            eventPublisher.publish(
                    new ReservationCanceledEvent(
                            reservation.getId(),
                            reservation.getSeat().getConcertDate().getConcert().getId()
                    )
            );
        }

        return new CancelReservationResult(
                reservation.getId(),
                reservation.getStatus().name(),
                reservation.getUpdatedAt()
        );
    }
}

