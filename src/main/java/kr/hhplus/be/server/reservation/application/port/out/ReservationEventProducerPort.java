package kr.hhplus.be.server.reservation.application.port.out;

import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
/**
 * Handles events raised by the reservation event flow.
 */

public interface ReservationEventProducerPort {
    void sendConfirmedEvent(ReservationConfirmedEvent event);
}
