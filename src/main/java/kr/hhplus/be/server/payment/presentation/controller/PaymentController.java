package kr.hhplus.be.server.payment.presentation.controller;

import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.presentation.dto.PaymentRequest;
import kr.hhplus.be.server.payment.presentation.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final MakePaymentUseCase makePaymentUseCase;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        validatePaymentRequest(request);
        var result = makePaymentUseCase.execute(new MakePaymentCommand(request.reservationId(), request.amount(), request.method()));
        var response = PaymentResponse.from(result);
        return ResponseEntity.ok(response);
    }

    private void validatePaymentRequest(PaymentRequest request) {
        // Validate controller input before constructing the application command.
        if (request == null) throw new IllegalArgumentException("Request is required");
        if (request.reservationId() == null) throw new IllegalArgumentException("ReservationId is required");
        if (request.amount() <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (request.method() == null) throw new IllegalArgumentException("Payment method is required");
    }
}
