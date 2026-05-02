package kr.hhplus.be.server.payment.presentation.controller;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
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
        if (request == null) throw new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");
        if (request.reservationId() == null) throw new ClientInputException(ErrorCode.RESERVATION_ID_REQUIRED, "예약 ID는 필수입니다.");
        if (request.amount() <= 0) throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_POSITIVE, "금액은 0보다 커야 합니다.");
        if (request.method() == null) throw new ClientInputException(ErrorCode.PAYMENT_METHOD_REQUIRED, "결제 수단은 필수입니다.");
    }
}
