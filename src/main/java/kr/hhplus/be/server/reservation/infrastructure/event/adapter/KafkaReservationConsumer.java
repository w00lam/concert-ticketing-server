package kr.hhplus.be.server.reservation.infrastructure.event.adapter;

import kr.hhplus.be.server.reservation.application.port.out.DataPlatformClientPort;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
/**
 * Adapts event infrastructure event flow to infrastructure components.
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaReservationConsumer {
    private final DataPlatformClientPort dataPlatformClientPort;

    @KafkaListener(topics = "reservation-confirmed", groupId = "data-platform-group")
    public void consumeReservation(ReservationConfirmedEvent event) {
        log.info("Received reservation confirmed event: {}", event.reservationId());

        // Preserve the concert id from Kafka so external aggregation uses the same key as ranking.
        dataPlatformClientPort.sendReservationConfirmed(event.reservationId(), event.concertId());
    }
}
