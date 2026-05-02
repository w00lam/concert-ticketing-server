package kr.hhplus.be.server.point.application.port.in;

public interface GetPointUseCase {
    GetPointResult execute(GetPointQuery query);
}
