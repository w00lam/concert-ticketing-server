package kr.hhplus.be.server.payment.application.service;

import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.out.PaymentRepositoryPort;
import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentExceptions;
import kr.hhplus.be.server.payment.domain.service.PaymentDomainService;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.reservation.application.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class PaymentProcessor {
    private final ReservationConfirmationService reservationConfirmationService;
    private final ReservationRepositoryPort reservationRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentDomainService paymentDomainService;
    private final PointDomainService pointDomainService;
    private final Clock clock;

    @Transactional
    public Payment process(MakePaymentCommand command) {
        paymentDomainService.validateAmount(command.amount());

        var existingPayment = paymentRepositoryPort.findByReservationId(command.reservationId());
        if (existingPayment.isPresent()) {
            return existingPayment(command, existingPayment.get());
        }

        Reservation pendingReservation = reservationRepositoryPort.findById(command.reservationId());
        pointDomainService.deduct(pendingReservation.getUser(), command.amount());

        Reservation reservation = reservationConfirmationService.confirm(command.reservationId());

        Payment payment = paymentDomainService.createPaid(reservation, command.amount(), command.method(), clock);

        return paymentRepositoryPort.save(payment);
    }

    private Payment existingPayment(MakePaymentCommand command, Payment payment) {
        if (!payment.hasSameRequest(command.amount(), command.method())) {
            throw PaymentExceptions.alreadyProcessed();
        }

        return payment;
    }
}
