package kr.hhplus.be.server.reservation.infrastructure.persistence;

import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Spring Data JPA repository for reservation entities.
 */


public interface JpaReservationRepository extends JpaRepository<Reservation, UUID> {
    @Modifying
    @Query("""
            UPDATE Reservation r SET r.status = 'CONFIRMED',
            r.confirmedAt = :now WHERE r.id = :reservationId
            AND r.status = 'TEMP_HOLD' AND r.tempHoldExpiresAt > :now
            """)
    int confirmIfNotExpired(
            @Param("reservationId") UUID reservationId,
            @Param("now") LocalDateTime now
    );

    // Only non-expired holds and confirmed reservations are active blockers for a seat.
    @Query("""
            SELECT COUNT(r) > 0 FROM Reservation r
            WHERE r.seat = :seat
            AND (
                r.status = 'CONFIRMED'
                OR (r.status = 'TEMP_HOLD' AND r.tempHoldExpiresAt > :now)
            )
            """)
    boolean existsActiveReservationBySeat(
            @Param("seat") Seat seat,
            @Param("now") LocalDateTime now
    );
}
