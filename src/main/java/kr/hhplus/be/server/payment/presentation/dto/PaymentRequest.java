package kr.hhplus.be.server.payment.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;

import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID reservationId,
        @Positive int amount,
        @NotNull PaymentMethod method
) {
}
