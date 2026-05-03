package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;
/**
 * Carries command values required by the point use case.
 */

public record DeductPointCommand(UUID userId, int amount) {
}
