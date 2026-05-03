package kr.hhplus.be.server.payment.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;

import java.util.UUID;
/**
 * Carries payment API response values.
 */

@Schema(description = "결제 생성 응답")
public record PaymentResponse(
        @Schema(description = "결제 ID")
        UUID paymentId,
        @Schema(description = "결제 상태", example = "PENDING")
        String status
) {
    public static PaymentResponse from(MakePaymentResult result) {
        return new PaymentResponse(result.paymentId(), result.status());
    }
}
