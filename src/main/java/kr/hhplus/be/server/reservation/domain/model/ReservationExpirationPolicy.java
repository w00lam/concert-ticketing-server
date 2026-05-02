package kr.hhplus.be.server.reservation.domain.model;

import java.time.LocalDateTime;

public interface ReservationExpirationPolicy {
    LocalDateTime expiresAt(LocalDateTime createdAt);
}
