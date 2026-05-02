package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.application.event.DomainEventPublisher;
import kr.hhplus.be.server.reservation.application.event.ReservationCanceledEvent;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationUseCase;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
            throw new IllegalStateException("Reservation already cancelled");
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

