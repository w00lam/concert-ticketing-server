package kr.hhplus.be.server.point.application.port.out;

import kr.hhplus.be.server.point.domain.model.Point;

import java.util.List;
import java.util.UUID;
/**
 * Defines the output port for point persistence.
 */

public interface PointRepositoryPort {
    List<Point> findAllByUserId(UUID userId);
    Point save(Point point);
}
