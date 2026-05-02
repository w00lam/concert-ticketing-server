package kr.hhplus.be.server.payment.presentation.dto;

import kr.hhplus.be.server.payment.domain.model.PaymentMethod;

import java.util.UUID;

public record PaymentRequest(UUID reservationId, int amount, PaymentMethod method) {
}
