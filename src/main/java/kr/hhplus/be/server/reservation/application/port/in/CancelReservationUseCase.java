package kr.hhplus.be.server.reservation.application.port.in;

public interface CancelReservationUseCase {
    CancelReservationResult execute(CancelReservationCommand command);
}
