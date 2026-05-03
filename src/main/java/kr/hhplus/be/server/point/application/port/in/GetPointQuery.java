package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;
/**
 * Carries query conditions required by the point use case.
 */

public record GetPointQuery(UUID userId) {
}
