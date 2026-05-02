package kr.hhplus.be.server.point.application.port.in;

public interface ChargePointUseCase {
    ChargePointResult execute(ChargePointCommand command);
}
