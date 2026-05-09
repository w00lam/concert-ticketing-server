package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.unit.fixture.ConcertFixture;
import kr.hhplus.be.server.unit.fixture.ReservationFixture;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReservationConfirmationServiceTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepository;

    @Mock
    DomainEventPublisher eventPublisher;

    @Mock
    Clock clock;

    @InjectMocks
    ReservationConfirmationService service;

    @Captor
    ArgumentCaptor<Object> eventCaptor;

    @Test
    void confirm_publishesReservationConfirmedEvent() {
        var concert = ConcertFixture.concert(fixedUUID());
        var concertDate = ConcertFixture.concertDate(concert);
        var seat = ConcertFixture.seat(concertDate);
        Reservation reservation = ReservationFixture.confirmed(fixedUUID2(), seat, fixedNow());

        freezeClock();
        when(reservationRepository.confirmIfNotExpired(fixedUUID2(), fixedNow())).thenReturn(true);
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
        freezeClock();
        when(reservationRepository.confirmIfNotExpired(fixedUUID(), fixedNow())).thenReturn(false);

        assertThatThrownBy(() -> service.confirm(fixedUUID()))
                .isInstanceOfSatisfying(BusinessRuleViolationException.class, exception ->
                        assertThat(exception.errorCode()).isEqualTo(ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED)
                );

        verify(eventPublisher, never()).publish(any());
    }

    private void freezeClock() {
        when(clock.instant()).thenReturn(fixedNow().toInstant(ZoneOffset.UTC));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }
}
