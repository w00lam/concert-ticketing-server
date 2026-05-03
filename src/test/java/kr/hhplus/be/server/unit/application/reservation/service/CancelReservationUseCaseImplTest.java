package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.application.event.DomainEventPublisher;
import kr.hhplus.be.server.reservation.application.event.ReservationCanceledEvent;
import kr.hhplus.be.server.reservation.application.port.in.CancelReservationCommand;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.CancelReservationUseCaseImpl;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

public class CancelReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;
    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    CancelReservationUseCaseImpl useCase;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Test
    void 확정된_예약을_취소하면_콘서트_예약_취소_이벤트가_발행된다() {
        // given
        Concert concert = Concert.builder().id(fixedUUID()).build();
        ConcertDate concertDate = ConcertDate.builder().concert(concert).build();
        Seat seat = Seat.builder().concertDate(concertDate).build();

        Reservation reservation = Reservation.builder()
                .id(fixedUUID2())
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(reservationRepository.findById(fixedUUID2())).thenReturn(reservation);

        // when
        useCase.execute(new CancelReservationCommand(fixedUUID2()));

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);

        verify(eventPublisher).publish(eventCaptor.capture());
        Object event = eventCaptor.getValue();

        assertThat(event).isInstanceOf(ReservationCanceledEvent.class);
        ReservationCanceledEvent canceledEvent = (ReservationCanceledEvent) event;

        assertThat(canceledEvent.reservationId()).isEqualTo(fixedUUID2());
        assertThat(canceledEvent.concertId()).isEqualTo(fixedUUID());
    }

    @Test
    void 확정되지_않은_예약을_취소하면_이벤트는_발행되지_않는다() {
        // given
        Reservation reservation = Reservation.builder()
                .id(fixedUUID())
                .status(ReservationStatus.TEMP_HOLD)
                .build();

        when(reservationRepository.findById(fixedUUID())).thenReturn(reservation);

        // when
        useCase.execute(new CancelReservationCommand(fixedUUID()));

        // then
        verify(eventPublisher, never()).publish(any());
    }
}
