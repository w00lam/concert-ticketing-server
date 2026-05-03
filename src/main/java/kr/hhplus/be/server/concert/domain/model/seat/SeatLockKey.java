package kr.hhplus.be.server.concert.domain.model.seat;

import java.util.UUID;

public class SeatLockKey {
    private SeatLockKey() {
    }

    public static String of(UUID concertId, UUID seatId) {
        return "lock:seat:" + concertId + ":" + seatId;
    }
}
