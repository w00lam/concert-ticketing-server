package kr.hhplus.be.server.point.application.port.out;

import kr.hhplus.be.server.point.domain.model.Point;

/**
 * Defines the output port for point persistence.
 */

public interface PointRepositoryPort {
    Point save(Point point);
}
