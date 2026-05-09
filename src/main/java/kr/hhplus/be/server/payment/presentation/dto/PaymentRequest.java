package kr.hhplus.be.server.payment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;

import java.util.UUID;

/**
 * Carries payment API request values.
 */
@Schema(description = "결제 생성 요청")
public record PaymentRequest(
        @Schema(description = "예약 ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull UUID reservationId,
        @Schema(description = "결제 금액", example = "50000", requiredMode = Schema.RequiredMode.REQUIRED)
        @Positive int amount,
        @Schema(description = "결제 수단", example = "CARD", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull PaymentMethod method
) {
    public MakePaymentCommand toCommand() {
        return new MakePaymentCommand(reservationId, amount, method);
    }
}
