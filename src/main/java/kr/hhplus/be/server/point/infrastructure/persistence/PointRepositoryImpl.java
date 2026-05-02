package kr.hhplus.be.server.point.infrastructure.persistence;

import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.infrastructure.persistence.JpaPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryPort {
    private final JpaPointRepository jpa;

    @Override
    public List<Point> findAllByUserId(UUID userId) {
        return jpa.findAllByUser_Id(userId);
    }

    @Override
    public Point save(Point point) {
        return jpa.save(point);
    }
}
