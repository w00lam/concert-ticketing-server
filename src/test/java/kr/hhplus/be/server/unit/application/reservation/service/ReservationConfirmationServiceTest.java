package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReservationConfirmationServiceTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;

    @Mock
    DomainEventPublisher eventPublisher;

    @InjectMocks
    ReservationConfirmationService service;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Test
    void confirm_publishesReservationConfirmedEvent() {
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
                .status(ReservationStatus.CONFIRMED)
                .confirmedAt(fixedNow())
                .build();

        when(reservationRepository.confirmIfNotExpired(fixedUUID2())).thenReturn(true);
        when(reservationRepository.findById(fixedUUID2())).thenReturn(reservation);

        Reservation result = service.confirm(fixedUUID2());

        assertThat(result).isEqualTo(reservation);

        verify(eventPublisher).publish(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertThat(event).isInstanceOf(ReservationConfirmedEvent.class);

        ReservationConfirmedEvent confirmedEvent = (ReservationConfirmedEvent) event;
        assertThat(confirmedEvent.reservationId()).isEqualTo(fixedUUID2());
        assertThat(confirmedEvent.concertId()).isEqualTo(fixedUUID());
    }

    @Test
    void confirm_doesNotPublishWhenReservationCannotBeConfirmed() {
        when(reservationRepository.confirmIfNotExpired(fixedUUID())).thenReturn(false);

        assertThatThrownBy(() -> service.confirm(fixedUUID()))
                .isInstanceOf(BusinessRuleViolationException.class);

        verify(eventPublisher, never()).publish(org.mockito.Mockito.any());
    }
}
