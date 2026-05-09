package kr.hhplus.be.server.unit.application.payment.service;

import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.service.MakePaymentUseCaseImpl;
import kr.hhplus.be.server.payment.application.service.PaymentProcessor;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.unit.fixture.PaymentFixture;
import kr.hhplus.be.server.unit.fixture.ReservationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MakePaymentUseCaseImplTest extends BaseUnitTest {
    @Mock
    PaymentProcessor paymentProcessor;

    @InjectMocks
    MakePaymentUseCaseImpl useCase;

    @Test
    @DisplayName("Payment use case returns processor result")
    void execute_returnsProcessorResult() {
        UUID reservationId = fixedUUID();
        int amount = 10000;
        var reservation = ReservationFixture.reservation(reservationId);
        Payment payment = PaymentFixture.paid(UUID.randomUUID(), reservation, amount, PaymentMethod.CARD);
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);

        when(paymentProcessor.process(command)).thenReturn(payment);

        MakePaymentResult result = useCase.execute(command);

        assertEquals(payment.getId(), result.paymentId());
        assertEquals("PAID", result.status());
        verify(paymentProcessor).process(command);
    }
}
