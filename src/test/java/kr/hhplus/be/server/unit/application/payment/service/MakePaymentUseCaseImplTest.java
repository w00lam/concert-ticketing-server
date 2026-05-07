package kr.hhplus.be.server.unit.application.payment.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.application.service.MakePaymentUseCaseImpl;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.PaymentStatus;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MakePaymentUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    @Mock
    PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    PaymentDomainService paymentDomainService;

    @Mock
    DomainEventPublisher eventPublisher;

    @Mock
    Clock clock;

    @InjectMocks
    MakePaymentUseCaseImpl useCase;

    @Test
    @DisplayName("Payment confirms reservation and publishes confirmation with concert id")
    void execute_success() {
        UUID reservationId = fixedUUID();
        UUID concertId = UUID.randomUUID();
        int amount = 10000;

        Concert concert = Concert.builder().id(concertId).title("concert").build();
        ConcertDate concertDate = ConcertDate.builder()
                .id(UUID.randomUUID())
                .concert(concert)
                .eventDate(LocalDate.now())
                .build();
        Seat seat = Seat.builder()
                .id(UUID.randomUUID())
                .concertDate(concertDate)
                .section("A")
                .row("1")
                .number("1")
                .grade("VIP")
                .build();
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .seat(seat)
                .build();
        Payment paidPayment = Payment.builder()
                .id(UUID.randomUUID())
                .status(PaymentStatus.PAID)
                .amount(amount)
                .reservation(reservation)
                .build();
        Payment savedPayment = Payment.builder()
                .id(UUID.randomUUID())
                .status(PaymentStatus.PAID)
                .amount(amount)
                .reservation(reservation)
                .build();
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(reservationRepositoryPort.confirmIfNotExpired(reservationId)).thenReturn(true);
        when(reservationRepositoryPort.findById(reservationId)).thenReturn(reservation);
        when(paymentDomainService.createPaid(reservation, amount, PaymentMethod.CARD, clock)).thenReturn(paidPayment);
        when(paymentRepositoryPort.save(paidPayment)).thenReturn(savedPayment);

        MakePaymentResult result = useCase.execute(command);

        assertEquals(savedPayment.getId(), result.paymentId());
        assertEquals(PaymentStatus.PAID.name(), result.status());

        ArgumentCaptor<ReservationConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(ReservationConfirmedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        verify(reservationRepositoryPort).confirmIfNotExpired(reservationId);

        // The event must carry concert id, not user id, because ranking and external consumers aggregate by concert.
        assertEquals(reservationId, eventCaptor.getValue().reservationId());
        assertEquals(concertId, eventCaptor.getValue().concertId());
    }

    @Test
    @DisplayName("Payment does not persist or publish when reservation confirmation fails")
    void execute_confirmationFailure() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(reservationRepositoryPort.confirmIfNotExpired(reservationId)).thenReturn(false);

        assertThrows(BusinessRuleViolationException.class, () -> useCase.execute(command));

        verify(reservationRepositoryPort).confirmIfNotExpired(reservationId);
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
        verify(eventPublisher, never()).publish(any());
    }
}
