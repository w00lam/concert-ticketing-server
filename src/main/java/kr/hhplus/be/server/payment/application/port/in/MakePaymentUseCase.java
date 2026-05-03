package kr.hhplus.be.server.payment.application.port.in;
/**
 * Defines the input port for the payment use case.
 */

public interface MakePaymentUseCase {
    MakePaymentResult execute(MakePaymentCommand command);
}
