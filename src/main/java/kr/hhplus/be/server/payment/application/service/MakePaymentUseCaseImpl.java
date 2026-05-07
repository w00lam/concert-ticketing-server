package kr.hhplus.be.server.payment.application.service;

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

        Reservation reservation = reservationConfirmationService.confirm(command.reservationId());

        Payment payment = paymentDomainService.createPaid(reservation, command.amount(), command.method(), clock);

        Payment saved = paymentRepositoryPort.save(payment);

        return new MakePaymentResult(saved.getId(), saved.getStatus().name());
    }
}
