package kr.hhplus.be.server.integration.infrastructure.event;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.infrastructure.event.adapter.KafkaReservationConsumer;
import kr.hhplus.be.server.user.domain.model.User;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@EmbeddedKafka(
        partitions = 1,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        topics = {"reservation-confirmed", "reservation-confirmed-dlt"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = "spring.kafka.listener.auto-startup=true")
public class KafkaReservationFailureIntegrationTest extends ReservationIntegrationTestBase {
    private static final String TOPIC = "reservation-confirmed";
    private static final String DLT_TOPIC = "reservation-confirmed-dlt";
    private static final String FAILURE_MESSAGE = "data platform is unavailable";

    @MockitoSpyBean
    private KafkaReservationConsumer kafkaReservationConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    @DisplayName("Consumer 처리가 반복 실패하면 2회 재시도 후 DLT로 이관된다")
    void failedConsumerRetriesTwiceThenPublishesToDlt() {
        User user = createUserWithPoints(50_000);
        ReservedSeat reservedSeat = createReservedSeat(user, "kafka failure concert");

        doThrow(new IllegalStateException(FAILURE_MESSAGE))
                .when(kafkaReservationConsumer)
                .consumeReservation(any(ReservationConfirmedEvent.class));

        try (Consumer<String, String> dltConsumer = createDltConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(dltConsumer, DLT_TOPIC);

            payReservation(reservedSeat.reservationId(), 50_000, PaymentMethod.CASH);

            Awaitility.await()
                    .atMost(10, TimeUnit.SECONDS)
                    .untilAsserted(() -> verify(kafkaReservationConsumer, times(3))
                            .consumeReservation(any(ReservationConfirmedEvent.class)));

            ConsumerRecords<String, String> dltRecords = KafkaTestUtils.getRecords(
                    dltConsumer,
                    Duration.ofSeconds(10)
            );
            Iterable<ConsumerRecord<String, String>> records = dltRecords.records(DLT_TOPIC);
            assertThat(records).isNotEmpty();
            ConsumerRecord<String, String> dltRecord = records.iterator().next();

            assertThat(dltRecord.topic()).isEqualTo(DLT_TOPIC);
            assertThat(dltRecord.key()).isEqualTo(reservedSeat.reservationId().toString());
            assertThat(dltRecord.value()).contains(reservedSeat.reservationId().toString());

            Header exceptionMessage = dltRecord.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);
            assertThat(exceptionMessage).isNotNull();
            assertThat(new String(exceptionMessage.value(), StandardCharsets.UTF_8))
                    .contains(FAILURE_MESSAGE);
        }
    }

    private Consumer<String, String> createDltConsumer() {
        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(
                "dlt-assertion-group",
                "false",
                embeddedKafkaBroker
        );
        consumerProperties.put("auto.offset.reset", "earliest");

        return new DefaultKafkaConsumerFactory<>(
                consumerProperties,
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer();
    }
}
