package kr.hhplus.be.server.reservation.infrastructure.event.adapter;

import kr.hhplus.be.server.reservation.application.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes reservation events to Kafka.
 */
@Component
@RequiredArgsConstructor
public class KafkaReservationEventProducer implements ReservationEventProducerPort {
    private static final String TOPIC = "reservation-confirmed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendConfirmedEvent(ReservationConfirmedEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.reservationId()), event);
    }
}
