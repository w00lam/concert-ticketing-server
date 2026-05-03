package kr.hhplus.be.server.payment.application.port.in;

import java.util.UUID;
/**
 * Carries the result returned by the payment use case.
 */

public record MakePaymentResult(UUID paymentId,String status) {
}
