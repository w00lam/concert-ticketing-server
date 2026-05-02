package kr.hhplus.be.server.reservation.application.port.in;

public interface MakeReservationUseCase {
    MakeReservationResult execute(MakeReservationCommand command);
}
