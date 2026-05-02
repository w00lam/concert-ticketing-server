package kr.hhplus.be.server.payment.presentation.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.presentation.ApiResponse;
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
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@Valid @RequestBody PaymentRequest request) {
        var result = makePaymentUseCase.execute(new MakePaymentCommand(request.reservationId(), request.amount(), request.method()));
        var response = PaymentResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
