package kr.hhplus.be.server.concert.application.port.in.seat;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
/**
 * Carries the result returned by the concert use case.
 */

public record GetSeatsResult(UUID seatId, String section, String row, String number,
                             String grade) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
