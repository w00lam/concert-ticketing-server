package kr.hhplus.be.server.payment.presentation.dto;

import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;

import java.util.UUID;

public record PaymentResponse(UUID paymentId, String status) {
    public static PaymentResponse from(MakePaymentResult result) {
        return new PaymentResponse(result.paymentId(), result.status());
    }
}
