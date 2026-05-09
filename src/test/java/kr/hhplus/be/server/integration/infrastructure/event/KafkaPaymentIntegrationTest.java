package kr.hhplus.be.server.integration.infrastructure.event;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.infrastructure.event.adapter.KafkaReservationConsumer;
import kr.hhplus.be.server.user.domain.model.User;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        topics = {"reservation-confirmed"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = "spring.kafka.listener.auto-startup=true")
public class KafkaPaymentIntegrationTest extends ReservationIntegrationTestBase {
    @MockitoSpyBean
    private KafkaReservationConsumer kafkaReservationConsumer;

    @Test
    @DisplayName("결제가 완료되면 예약 확정 이벤트가 Kafka Consumer까지 전달된다")
    void paymentPublishesReservationConfirmedEventToKafkaConsumer() {
        User user = createUserWithPoints(50_000);
        ReservedSeat reservedSeat = createReservedSeat(user, "kafka payment concert");

        payReservation(reservedSeat.reservationId(), 50_000, PaymentMethod.CASH);

        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(kafkaReservationConsumer).consumeReservation(argThat(event ->
                                matchesReservationConfirmedEvent(event, reservedSeat)
                        ))
                );
    }

    private boolean matchesReservationConfirmedEvent(ReservationConfirmedEvent event, ReservedSeat reservedSeat) {
        return event != null
                && event.reservationId().equals(reservedSeat.reservationId())
                && event.concertId().equals(reservedSeat.concertId());
    }
}
