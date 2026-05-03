package kr.hhplus.be.server.reservation.application.port.in;

public interface ConfirmReservationUseCase {
    ConfirmReservationResult execute(ConfirmReservationCommand command);
}
