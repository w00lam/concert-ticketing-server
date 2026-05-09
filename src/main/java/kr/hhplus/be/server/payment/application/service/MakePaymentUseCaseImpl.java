package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implements the payment use case and delegates payment processing.
 */

@Service
@RequiredArgsConstructor
public class MakePaymentUseCaseImpl implements MakePaymentUseCase {
    private final PaymentProcessor paymentProcessor;

    @Override
    public MakePaymentResult execute(MakePaymentCommand command) {
        Payment payment = paymentProcessor.process(command);

        return new MakePaymentResult(payment.getId(), payment.getStatus().name());
    }
}
