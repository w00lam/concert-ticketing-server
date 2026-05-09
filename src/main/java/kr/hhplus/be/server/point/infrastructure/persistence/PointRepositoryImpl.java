package kr.hhplus.be.server.point.infrastructure.persistence;

import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.domain.model.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Implements the point repository output port with JPA.
 */

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryPort {
    private final JpaPointRepository jpa;

    @Override
    public Point save(Point point) {
        return jpa.save(point);
    }
}
