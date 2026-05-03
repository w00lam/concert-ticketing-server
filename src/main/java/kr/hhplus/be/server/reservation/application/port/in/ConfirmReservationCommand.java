package kr.hhplus.be.server.reservation.application.port.in;

import java.util.UUID;
/**
 * Carries command values required by the reservation use case.
 */

public record ConfirmReservationCommand(UUID reservationId) {
}
