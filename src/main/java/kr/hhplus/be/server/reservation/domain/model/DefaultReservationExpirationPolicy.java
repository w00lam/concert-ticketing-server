package kr.hhplus.be.server.reservation.domain.model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DefaultReservationExpirationPolicy implements ReservationExpirationPolicy {
    @Override
    public LocalDateTime expiresAt(LocalDateTime createdAt) {
        return createdAt.plusMinutes(5); // 기본 5분
    }
}
