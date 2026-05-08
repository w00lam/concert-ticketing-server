package kr.hhplus.be.server.payment.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PAYMENTS",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_payment_reservation",
                columnNames = "reservation_id"
        ),
        indexes = {
                @Index(name = "idx_status", columnList = "status")
        })
/**
 * Represents core state and rules in the payment domain.
 */
public class Payment {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    public static Payment createPending(Reservation reservation, int amount, PaymentMethod method) {
        return Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PENDING)
                .paidAt(null)
                .deleted(false)
                .build();
    }

    public static Payment createPaid(Reservation reservation, int amount, PaymentMethod method, Clock clock) {
        return Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now(clock))
                .deleted(false)
                .build();
    }

    public boolean hasSameRequest(int amount, PaymentMethod method) {
        return this.amount == amount && this.method == method;
    }
}
