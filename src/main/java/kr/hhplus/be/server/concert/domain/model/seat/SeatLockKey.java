package kr.hhplus.be.server.concert.domain.model.seat;

import java.util.UUID;
/**
 * Creates lock keys for seat reservation attempts.
 */

public class SeatLockKey {
    private SeatLockKey() {
    }

    public static String of(UUID concertId, UUID seatId) {
        return "lock:seat:" + concertId + ":" + seatId;
    }
}
