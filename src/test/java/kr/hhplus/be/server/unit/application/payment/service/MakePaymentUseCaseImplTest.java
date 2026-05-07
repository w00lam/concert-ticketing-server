package kr.hhplus.be.server.unit.application.payment.service;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.application.service.MakePaymentUseCaseImpl;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.PaymentStatus;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.util.Optional;
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
    ReservationConfirmationService reservationConfirmationService;

    @Mock
    PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    PaymentDomainService paymentDomainService;

    @Mock
    Clock clock;

    @InjectMocks
    MakePaymentUseCaseImpl useCase;

    @Test
    @DisplayName("Payment confirms reservation before creating paid payment")
    void execute_success() {
        UUID reservationId = fixedUUID();
        int amount = 10000;

        Reservation reservation = Reservation.builder()
                .id(reservationId)
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
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.empty());
        when(reservationConfirmationService.confirm(reservationId)).thenReturn(reservation);
        when(paymentDomainService.createPaid(reservation, amount, PaymentMethod.CARD, clock)).thenReturn(paidPayment);
        when(paymentRepositoryPort.save(paidPayment)).thenReturn(savedPayment);

        MakePaymentResult result = useCase.execute(command);

        assertEquals(savedPayment.getId(), result.paymentId());
        assertEquals(PaymentStatus.PAID.name(), result.status());

        verify(reservationConfirmationService).confirm(reservationId);
    }

    @Test
    @DisplayName("Payment does not persist or publish when reservation confirmation fails")
    void execute_confirmationFailure() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.empty());
        when(reservationConfirmationService.confirm(reservationId))
                .thenThrow(BusinessRuleViolationException.class);

        assertThrows(BusinessRuleViolationException.class, () -> useCase.execute(command));

        verify(reservationConfirmationService).confirm(reservationId);
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment returns existing payment for duplicate same request")
    void execute_duplicateSameRequest() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        Payment existingPayment = Payment.builder()
                .id(UUID.randomUUID())
                .amount(amount)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PAID)
                .build();
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.of(existingPayment));

        MakePaymentResult result = useCase.execute(command);

        assertEquals(existingPayment.getId(), result.paymentId());
        assertEquals(PaymentStatus.PAID.name(), result.status());

        verify(reservationConfirmationService, never()).confirm(any());
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment rejects duplicate request with different amount")
    void execute_duplicateDifferentRequest() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        Payment existingPayment = Payment.builder()
                .id(UUID.randomUUID())
                .amount(amount)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PAID)
                .build();
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount + 1, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount + 1);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.of(existingPayment));

        assertThrows(BusinessRuleViolationException.class, () -> useCase.execute(command));

        verify(reservationConfirmationService, never()).confirm(any());
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }
}
