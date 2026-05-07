package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
/**
 * Implements the payment use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class MakePaymentUseCaseImpl implements MakePaymentUseCase {
    private final ReservationConfirmationService reservationConfirmationService;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;
    private final Clock clock;

    @Override
    @Transactional
    public MakePaymentResult execute(MakePaymentCommand command) {
        paymentDomainService.validateAmount(command.amount());

        var existingPayment = paymentRepositoryPort.findByReservationId(command.reservationId());
        if (existingPayment.isPresent()) {
            return resultFromExisting(command, existingPayment.get());
        }

        Reservation reservation = reservationConfirmationService.confirm(command.reservationId());

        Payment payment = paymentDomainService.createPaid(reservation, command.amount(), command.method(), clock);

        Payment saved = paymentRepositoryPort.save(payment);

        return new MakePaymentResult(saved.getId(), saved.getStatus().name());
    }

    private MakePaymentResult resultFromExisting(MakePaymentCommand command, Payment payment) {
        if (!payment.hasSameRequest(command.amount(), command.method())) {
            throw new BusinessRuleViolationException(
                    ErrorCode.PAYMENT_ALREADY_PROCESSED,
                    "\uC774\uBBF8 \uACB0\uC81C\uB41C \uC608\uC57D\uC785\uB2C8\uB2E4."
            );
        }

        return new MakePaymentResult(payment.getId(), payment.getStatus().name());
    }
}
