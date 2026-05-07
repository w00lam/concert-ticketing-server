package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.common.application.event.DomainEventPublisher;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
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
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;

    @Override
    @Transactional
    public MakePaymentResult execute(MakePaymentCommand command) {
        paymentDomainService.validateAmount(command.amount());

        boolean confirmed = reservationRepositoryPort.confirmIfNotExpired(command.reservationId());
        if (!confirmed) {
            throw new BusinessRuleViolationException(
                    ErrorCode.RESERVATION_EXPIRED_OR_PROCESSED,
                    "예약이 만료되었거나 이미 처리되었습니다."
            );
        }

        Reservation reservation = reservationRepositoryPort.findById(command.reservationId());

        Payment payment = paymentDomainService.createPaid(reservation, command.amount(), command.method(), clock);

        Payment saved = paymentRepositoryPort.save(payment);

        // Downstream ranking and data-platform consumers aggregate confirmations by concert.
        eventPublisher.publish(new ReservationConfirmedEvent(
                reservation.getId(),
                reservation.getSeat().getConcertDate().getConcert().getId()
        ));

        return new MakePaymentResult(saved.getId(), saved.getStatus().name());
    }
}
