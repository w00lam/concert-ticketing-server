package kr.hhplus.be.server.point.domain.service;

import kr.hhplus.be.server.common.domain.AmountValidator;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * Encapsulates domain rules for the point feature.
 */

@Component
public class PointDomainService {
    public Point createCharge(User user, int amount) {
        AmountValidator.requirePositive(amount);
        return Point.createCharge(user, amount);
    }

    public int calculateBalance(List<Point> points) {
        return points.stream()
                .filter(tx -> !tx.isDeleted())
                .mapToInt(Point::getAmount)
                .sum();
    }
}
