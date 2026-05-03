package kr.hhplus.be.server.point.application.port.in;
/**
 * Defines the input port for the point use case.
 */

public interface DeductPointUseCase {
    DeductPointResult execute(DeductPointCommand command);
}
