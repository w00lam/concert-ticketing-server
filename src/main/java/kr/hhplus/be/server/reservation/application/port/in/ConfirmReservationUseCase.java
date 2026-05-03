package kr.hhplus.be.server.reservation.application.port.in;
/**
 * Defines the input port for the reservation use case.
 */

public interface ConfirmReservationUseCase {
    ConfirmReservationResult execute(ConfirmReservationCommand command);
}
