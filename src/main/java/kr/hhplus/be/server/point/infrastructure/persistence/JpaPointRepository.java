package kr.hhplus.be.server.point.infrastructure.persistence;

import kr.hhplus.be.server.point.domain.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for point entities.
 */

public interface JpaPointRepository extends JpaRepository<Point, UUID> {
    List<Point> findAllByUser_Id(UUID userId);
}
