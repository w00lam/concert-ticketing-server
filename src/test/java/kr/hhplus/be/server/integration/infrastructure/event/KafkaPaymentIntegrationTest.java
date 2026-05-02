package kr.hhplus.be.server.integration.infrastructure.event;

import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.infrastructure.event.adapter.KafkaReservationConsumer;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@EmbeddedKafka(
        partitions = 1,
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" },
        topics = { "reservation-confirmed" }
)
public class KafkaPaymentIntegrationTest extends ReservationIntegrationTestBase {
    @MockitoSpyBean
    private KafkaReservationConsumer kafkaReservationConsumer;

    @Test
    @DisplayName("결제가 완료되면 카프카를 통해 예약 확정 이벤트가 컨슈머까지 전달되어야 한다")
    void should_PublishAndConsumeKafkaEvent_When_PaymentCompleted() {
        // 1. Given: 사용자 생성 및 좌석 예약
        User user = createUserWithPoints(50000);
        Seat seat = createSeat(); // 내부에서 콘서트 및 일자 생성 포함됨

        MakeReservationResult reservationResult = reserveSeat(
                user.getId(),
                seat.getConcertDate().getConcert().getId(),
                seat.getId()
        );

        // 2. When: 결제 실행 (이 내부 로직이 Spring Event를 거쳐 Kafka Producer를 호출함)
        payReservation(
                reservationResult.reservationId(),
                50000,
                PaymentMethod.CASH
        );

        // 3. Then: Awaitility를 사용하여 비동기로 카프카 메시지가 컨슈머에 도달하는지 검증
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS) // 카프카 처리는 약간의 지연이 있을 수 있음
                .untilAsserted(() -> {
                    verify(kafkaReservationConsumer, timeout(10000))
                            .consumeReservation(any(ReservationConfirmedEvent.class));
                });
    }
}
