package kr.hhplus.be.server.unit.fixture;

import kr.hhplus.be.server.payment.domain.model.Payment;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.payment.domain.model.PaymentStatus;
import kr.hhplus.be.server.reservation.domain.model.Reservation;

import java.time.LocalDateTime;
import java.util.UUID;

public final class PaymentFixture {
    private PaymentFixture() {
    }

    public static Payment paid(UUID id, Reservation reservation, int amount, PaymentMethod method) {
        return Payment.builder()
                .id(id)
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PAID)
                .build();
    }

    public static Payment paid(UUID id, Reservation reservation, int amount, PaymentMethod method, LocalDateTime paidAt) {
        return Payment.builder()
                .id(id)
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PAID)
                .paidAt(paidAt)
                .deleted(false)
                .build();
    }

    public static Payment paidRequest(UUID id, int amount, PaymentMethod method) {
        return Payment.builder()
                .id(id)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PAID)
                .build();
    }
}
