package kr.hhplus.be.server.point.application.port.in;

import java.util.UUID;
/**
 * Carries the result returned by the point use case.
 */

public record GetPointResult(UUID userId, int balance) {
}
