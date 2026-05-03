package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;

public record ChargePointCommand(UUID userId, int amount) {
}
