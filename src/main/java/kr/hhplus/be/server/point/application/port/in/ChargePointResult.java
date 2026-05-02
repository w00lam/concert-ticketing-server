package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;

public record ChargePointResult(UUID userId, int balance) {
}
