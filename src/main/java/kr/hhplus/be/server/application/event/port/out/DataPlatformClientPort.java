package kr.hhplus.be.server.application.event.port.out;

import java.util.UUID;
/**
 * Handles events raised by the reservation event flow.
 */

public interface DataPlatformClientPort {
    void sendReservationConfirmed(UUID reservationId, UUID concertId);

    void sendReservationCanceled(UUID reservationId, UUID concertId);
}
