package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.ConfirmReservationUseCaseImpl;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class ConfirmReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;
    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    ConfirmReservationUseCaseImpl useCase;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Test
    void 예약이_확정되면_ReservationConfirmedEvent가_발행된다() {
        // given
        Concert concert = Concert.builder()
                .id(fixedUUID())
                .build();

        ConcertDate concertDate = ConcertDate.builder()
                .concert(concert)
                .build();

        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .build();

        Reservation reservation = Reservation.builder()
                .id(fixedUUID2())
                .seat(seat)
                .status(ReservationStatus.TEMP_HOLD)
                .tempHoldExpiresAt(fixedNow().plusMinutes(10))
                .build();

        when(reservationRepository.confirmIfNotExpired(fixedUUID2())).thenReturn(true);
        when(reservationRepository.findById(fixedUUID2())).thenReturn(reservation);

        // when
        useCase.execute(new ConfirmReservationCommand(fixedUUID2()));

        // then
        verify(eventPublisher).publish(eventCaptor.capture());

        Object event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(ReservationConfirmedEvent.class);

        ReservationConfirmedEvent confirmedEvent =
                (ReservationConfirmedEvent) event;

        assertThat(confirmedEvent.reservationId()).isEqualTo(fixedUUID2());
        assertThat(confirmedEvent.concertId()).isEqualTo(fixedUUID());
    }

    @Test
    void 이미_처리된_예약이면_이벤트는_발행되지_않는다() {
        // given
        when(reservationRepository.confirmIfNotExpired(fixedUUID()))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() ->
                useCase.execute(new ConfirmReservationCommand(fixedUUID()))
        ).isInstanceOf(IllegalStateException.class);

        verify(eventPublisher, org.mockito.Mockito.never()).publish(org.mockito.Mockito.any());
    }
}
