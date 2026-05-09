package kr.hhplus.be.server.integration.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.reservation.application.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.user.domain.model.User;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public class ReservationEventListenerIntegrationTest extends ReservationIntegrationTestBase {
    @MockitoBean
    ReservationEventProducerPort reservationEventProducerPort;

    @Test
    void 결제_완료_후_예약확정_이벤트가_발행되고_카프카_프로듀서가_호출된다() {
        User user = createUserWithPoints(10_000);
        Seat seat = createSeat();

        var reservationResult = reserveSeat(
                user.getId(),
                seat.getConcertDate().getConcert().getId(),
                seat.getId()
        );

        payReservation(reservationResult.reservationId(), 5_000, PaymentMethod.CARD);

        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(reservationEventProducerPort, times(1))
                                .sendConfirmedEvent(any(ReservationConfirmedEvent.class))
                );
    }
}
