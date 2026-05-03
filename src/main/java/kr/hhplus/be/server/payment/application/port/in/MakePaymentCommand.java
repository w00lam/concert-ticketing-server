package kr.hhplus.be.server.payment.application.port.in;

import kr.hhplus.be.server.payment.domain.model.PaymentMethod;

import java.util.UUID;

public record MakePaymentCommand(UUID reservationId, int amount, PaymentMethod method) {
}
