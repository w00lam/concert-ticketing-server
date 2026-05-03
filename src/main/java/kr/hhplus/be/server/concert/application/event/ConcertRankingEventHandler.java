package kr.hhplus.be.server.concert.application.event;

import kr.hhplus.be.server.concert.application.service.GetConcertRankingService;
import kr.hhplus.be.server.reservation.application.event.ReservationCanceledEvent;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
/**
 * Handles events raised by the reservation event flow.
 */

@Component
@RequiredArgsConstructor
public class ConcertRankingEventHandler {
    private final GetConcertRankingService rankingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConfirmed(ReservationConfirmedEvent event) {
        rankingService.increaseReservation(event.concertId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCanceled(ReservationCanceledEvent event) {
        rankingService.decreaseReservation(event.concertId());
    }
}
