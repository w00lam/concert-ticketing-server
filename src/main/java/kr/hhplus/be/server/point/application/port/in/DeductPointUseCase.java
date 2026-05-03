package kr.hhplus.be.server.point.application.port.in;

public interface DeductPointUseCase {
    DeductPointResult execute(DeductPointCommand command);
}
