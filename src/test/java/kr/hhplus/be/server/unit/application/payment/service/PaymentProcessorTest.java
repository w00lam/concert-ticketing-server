package kr.hhplus.be.server.unit.application.payment.service;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.application.service.PaymentProcessor;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationExceptions;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.unit.fixture.PaymentFixture;
import kr.hhplus.be.server.unit.fixture.ReservationFixture;
import kr.hhplus.be.server.unit.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentProcessorTest extends BaseUnitTest {
    @Mock
    ReservationConfirmationService reservationConfirmationService;

    @Mock
    ReservationRepositoryPort reservationRepositoryPort;

    @Mock
    PaymentRepositoryPort paymentRepositoryPort;

    @Mock
    PaymentDomainService paymentDomainService;

    @Mock
    PointDomainService pointDomainService;

    @Mock
    Clock clock;

    @InjectMocks
    PaymentProcessor paymentProcessor;

    @Test
    @DisplayName("Payment processor confirms reservation before creating paid payment")
    void process_success() {
        UUID reservationId = fixedUUID();
        int amount = 10000;

        var user = UserFixture.user(UUID.randomUUID(), 20000);
        Reservation reservation = ReservationFixture.reservation(reservationId, user);
        Payment paidPayment = PaymentFixture.paid(UUID.randomUUID(), reservation, amount, PaymentMethod.CARD);
        Payment savedPayment = PaymentFixture.paid(UUID.randomUUID(), reservation, amount, PaymentMethod.CARD);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.empty());
        when(reservationRepositoryPort.findById(reservationId)).thenReturn(reservation);
        doAnswer(invocation -> {
            user.deductPoints(amount);
            return null;
        }).when(pointDomainService).deduct(user, amount);
        when(reservationConfirmationService.confirm(reservationId)).thenReturn(reservation);
        when(paymentDomainService.createPaid(reservation, amount, PaymentMethod.CARD, clock)).thenReturn(paidPayment);
        when(paymentRepositoryPort.save(paidPayment)).thenReturn(savedPayment);

        Payment result = paymentProcessor.process(command);

        assertSame(savedPayment, result);
        assertEquals(10000, user.getPoints());
        verify(pointDomainService).deduct(user, amount);
        verify(reservationConfirmationService).confirm(reservationId);
    }

    @Test
    @DisplayName("Payment processor does not persist or publish when reservation confirmation fails")
    void process_confirmationFailure() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        var user = UserFixture.user(UUID.randomUUID(), 20000);
        Reservation reservation = ReservationFixture.reservation(reservationId, user);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.empty());
        when(reservationRepositoryPort.findById(reservationId)).thenReturn(reservation);
        doAnswer(invocation -> {
            user.deductPoints(amount);
            return null;
        }).when(pointDomainService).deduct(user, amount);
        when(reservationConfirmationService.confirm(reservationId))
                .thenThrow(ReservationExceptions.expiredOrProcessed());

        BusinessRuleViolationException exception =
                assertThrows(BusinessRuleViolationException.class, () -> paymentProcessor.process(command));

        assertEquals(ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED, exception.errorCode());
        assertEquals(10000, user.getPoints());
        verify(pointDomainService).deduct(user, amount);
        verify(reservationConfirmationService).confirm(reservationId);
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment processor stops before confirmation when user has insufficient points")
    void process_insufficientPoints() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        var user = UserFixture.user(UUID.randomUUID(), 5000);
        Reservation reservation = ReservationFixture.reservation(reservationId, user);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.empty());
        when(reservationRepositoryPort.findById(reservationId)).thenReturn(reservation);
        doAnswer(invocation -> {
            user.deductPoints(amount);
            return null;
        }).when(pointDomainService).deduct(user, amount);

        BusinessRuleViolationException exception =
                assertThrows(BusinessRuleViolationException.class, () -> paymentProcessor.process(command));

        assertEquals(ErrorCode.INSUFFICIENT_POINTS, exception.errorCode());
        assertEquals(5000, user.getPoints());
        verify(pointDomainService).deduct(user, amount);
        verify(reservationConfirmationService, never()).confirm(any());
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment processor returns existing payment for duplicate same request")
    void process_duplicateSameRequest() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        Payment existingPayment = PaymentFixture.paidRequest(UUID.randomUUID(), amount, PaymentMethod.CARD);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.of(existingPayment));

        Payment result = paymentProcessor.process(command);

        assertSame(existingPayment, result);
        verify(pointDomainService, never()).deduct(any(), anyInt());
        verify(reservationConfirmationService, never()).confirm(any());
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Payment processor rejects duplicate request with different amount")
    void process_duplicateDifferentRequest() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        Payment existingPayment = PaymentFixture.paidRequest(UUID.randomUUID(), amount, PaymentMethod.CARD);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount + 1, PaymentMethod.CARD);

        doNothing().when(paymentDomainService).validateAmount(amount + 1);
        when(paymentRepositoryPort.findByReservationId(reservationId)).thenReturn(Optional.of(existingPayment));

        BusinessRuleViolationException exception =
                assertThrows(BusinessRuleViolationException.class, () -> paymentProcessor.process(command));

        assertEquals(ErrorCode.PAYMENT_ALREADY_PROCESSED, exception.errorCode());
        verify(pointDomainService, never()).deduct(any(), eq(amount + 1));
        verify(reservationConfirmationService, never()).confirm(any());
        verify(paymentRepositoryPort, never()).save(any(Payment.class));
    }
}
