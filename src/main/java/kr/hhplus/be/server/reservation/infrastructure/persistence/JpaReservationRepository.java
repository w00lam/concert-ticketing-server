package kr.hhplus.be.server.reservation.infrastructure.persistence;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;


public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {
    @Modifying
    @Query("""
            UPDATE Reservation r SET r.status = 'CONFIRMED',
            r.confirmedAt = CURRENT_TIMESTAMP WHERE r.id = :reservationId
            AND r.status = 'TEMP_HOLD' AND r.tempHoldExpiresAt > CURRENT_TIMESTAMP
            """)
    int confirmIfNotExpired(@Param("reservationId") UUID reservationId);

    @Modifying
    @Query("UPDATE Reservation r SET r.status = 'EXPIRED' WHERE r.status = 'TEMP_HOLD'AND r.tempHoldExpiresAt <= CURRENT_TIMESTAMP")
    int expireAllExpired();

    @Modifying
    @Query("UPDATE Reservation r SET r.tempHoldExpiresAt = :expiresAt WHERE r.id = :reservationId")
    void extendExpiration(@Param("reservationId") UUID reservationId, @Param("expiresAt") LocalDateTime expiresAt);

    boolean existsBySeatAndStatus(Seat seat, ReservationStatus status);

    // Only non-expired holds and confirmed reservations are active blockers for a seat.
    @Query("""
            SELECT COUNT(r) > 0 FROM Reservation r
            WHERE r.seat = :seat
            AND (
                r.status = 'CONFIRMED'
                OR (r.status = 'TEMP_HOLD' AND r.tempHoldExpiresAt > CURRENT_TIMESTAMP)
            )
            """)
    boolean existsActiveReservationBySeat(@Param("seat") Seat seat);

    long countBySeatAndStatus(Seat seat, ReservationStatus status);

    @Modifying
    @Query("UPDATE Reservation r SET r.tempHoldExpiresAt = :expiredAt WHERE r.id = :reservationId")
    void forceExpire(@Param("reservationId") UUID reservationId, @Param("expiredAt") LocalDateTime expiredAt);

}
