package kr.hhplus.be.server.reservation.application.port.in;
/**
 * Defines the input port for the reservation use case.
 */

public interface MakeReservationUseCase {
    MakeReservationResult execute(MakeReservationCommand command);
}
