package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.application.event.port.out.DataPlatformClientPort;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ReservationEventListenerIntegrationTest extends ReservationIntegrationTestBase {
    @MockitoBean
    DataPlatformClientPort dataPlatformClient;

    @Autowired
    private ConfirmReservationUseCase confirmReservationUseCase;

    @Test
    void 결제_완료_후_예약확정_이벤트가_발행되고_외부시스템이_호출된다() {
        // given
        User user = createUserWithPoints(10_000);
        Seat seat = createSeat();

        // when: 예약
        var reservationResult = reserveSeat(
                user.getId(),
                seat.getConcertDate().getConcert().getId(),
                seat.getId()
        );

        // when: 결제 → 예약 확정
        payReservation(reservationResult.reservationId(), 5_000, PaymentMethod.CARD);
        confirmReservationUseCase.execute(new ConfirmReservationCommand(reservationResult.reservationId()));

        // then: 트랜잭션 커밋 이후 이벤트 리스너가 외부 시스템 호출
        verify(dataPlatformClient, times(1))
                .sendReservationConfirmed(reservationResult.reservationId(), seat.getConcertDate().getConcert().getId());
    }
}
