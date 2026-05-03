package kr.hhplus.be.server.reservation.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.user.domain.model.User;
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
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "RESERVATIONS",
        indexes = {
                @Index(name = "idx_status_tempHoldExpiresAt", columnList = "status,tempHoldExpiresAt")
        }
)
/**
 * Represents core state and rules in the reservation domain.
 */
public class Reservation {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private LocalDateTime tempHoldExpiresAt;

    private LocalDateTime confirmedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;


    public static Reservation create(User user, Seat seat, Clock clock, ReservationExpirationPolicy expirationPolicy) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expires = expirationPolicy.expiresAt(now);

        return Reservation.builder()
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_HOLD)
                .tempHoldExpiresAt(expires)
                .confirmedAt(null)
                .deleted(false)
                .build();
    }
    public void cancel() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new BusinessRuleViolationException(ErrorCode.RESERVATION_ALREADY_CANCELLED, "이미 취소된 예약입니다.");
        }

        this.status = ReservationStatus.CANCELED;
        this.deleted = true;
    }
}
