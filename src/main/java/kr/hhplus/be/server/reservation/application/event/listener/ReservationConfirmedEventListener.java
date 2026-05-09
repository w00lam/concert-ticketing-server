package kr.hhplus.be.server.reservation.application.event.listener;

import kr.hhplus.be.server.reservation.application.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Forwards committed reservation confirmation events to external message infrastructure.
 */
@Component
@RequiredArgsConstructor
public class ReservationConfirmedEventListener {
    private final ReservationEventProducerPort reservationEventProducerPort;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationConfirmedEvent event) {
        reservationEventProducerPort.sendConfirmedEvent(event);
    }
}
