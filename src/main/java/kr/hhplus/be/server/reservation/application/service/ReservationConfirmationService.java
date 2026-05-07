package kr.hhplus.be.server.reservation.application.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationConfirmationService {
    private static final String EXPIRED_OR_PROCESSED_MESSAGE =
            "\uC608\uC57D\uC774 \uB9CC\uB8CC\uB418\uC5C8\uAC70\uB098 \uC774\uBBF8 \uCC98\uB9AC\uB418\uC5C8\uC2B5\uB2C8\uB2E4.";

    private final ReservationRepositoryPort reservationRepository;
    private final DomainEventPublisher eventPublisher;

    public Reservation confirm(UUID reservationId) {
        boolean success = reservationRepository.confirmIfNotExpired(reservationId);

        if (!success) {
            throw new BusinessRuleViolationException(
                    ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED,
                    EXPIRED_OR_PROCESSED_MESSAGE
            );
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
