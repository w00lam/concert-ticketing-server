package kr.hhplus.be.server.payment.application.port.in;

import java.util.UUID;

public record MakePaymentResult(UUID paymentId,String status) {
}
