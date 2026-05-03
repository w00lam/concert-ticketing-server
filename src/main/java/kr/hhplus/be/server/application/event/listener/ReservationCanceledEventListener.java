package kr.hhplus.be.server.application.event.listener;

import kr.hhplus.be.server.application.event.port.out.DataPlatformClientPort;
import kr.hhplus.be.server.reservation.application.event.ReservationCanceledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
/**
 * Listens to reservation event events and triggers follow-up processing.
 */

@Component
@RequiredArgsConstructor
public class ReservationCanceledEventListener {
    private final DataPlatformClientPort dataPlatformClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReservationCanceledEvent event) {
        dataPlatformClient.sendReservationCanceled(event.reservationId(), event.concertId());
    }
}
