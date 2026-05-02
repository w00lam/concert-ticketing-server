package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;

public record DeductPointResult(UUID userId, int deductedAmount, int remainingPoints) {
}
