package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationConfirmationService {
    private final ReservationRepositoryPort reservationRepository;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;

    public Reservation confirm(UUID reservationId) {
        LocalDateTime now = LocalDateTime.now(clock);
        boolean success = reservationRepository.confirmIfNotExpired(reservationId, now);

        if (!success) {
            throw ReservationExceptions.expiredOrProcessed();
        }

        Reservation reservation = reservationRepository.findById(reservationId);

        eventPublisher.publish(
                new ReservationConfirmedEvent(
                        reservation.getId(),
                        reservation.getSeat().getConcertDate().getConcert().getId()
                )
        );

        return reservation;
    }
}
