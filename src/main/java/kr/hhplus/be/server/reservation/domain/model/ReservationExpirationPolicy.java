package kr.hhplus.be.server.reservation.domain.model;

import java.time.LocalDateTime;
/**
 * Defines how temporary reservation expiration times are calculated.
 */

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
