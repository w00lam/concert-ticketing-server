package kr.hhplus.be.server.payment.application.port.in;

public interface MakePaymentUseCase {
    MakePaymentResult execute(MakePaymentCommand command);
}
