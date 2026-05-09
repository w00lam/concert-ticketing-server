package kr.hhplus.be.server.reservation.domain.model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
/**
 * Calculates the default expiration time for temporary reservations.
 */

@Component
public class DefaultReservationExpirationPolicy implements ReservationExpirationPolicy {
    @Override
    public LocalDateTime expiresAt(LocalDateTime createdAt) {
        return createdAt.plusMinutes(5); // 기본 5분
    }
}
