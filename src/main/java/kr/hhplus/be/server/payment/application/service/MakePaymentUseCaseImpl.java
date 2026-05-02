package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.concert.application.service.GetConcertRankingService;
import kr.hhplus.be.server.application.event.DomainEventPublisher;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MakePaymentUseCaseImpl implements MakePaymentUseCase {
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;
    private final DomainEventPublisher eventPublisher;

    @Override
    public MakePaymentResult execute(MakePaymentCommand command) {
        Reservation reservation = reservationRepositoryPort.findById(command.reservationId());

        paymentDomainService.validateAmount(command.amount());

        Payment payment = paymentDomainService.createPending(reservation, command.amount(), command.method());

        Payment saved = paymentRepositoryPort.save(payment);

        // Downstream ranking and data-platform consumers aggregate confirmations by concert.
        eventPublisher.publish(new ReservationConfirmedEvent(
                reservation.getId(),
                reservation.getSeat().getConcertDate().getConcert().getId()
        ));

        return new MakePaymentResult(saved.getId(), saved.getStatus().name());
    }
}
