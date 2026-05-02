package kr.hhplus.be.server.point.domain.service;

import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.domain.user.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointDomainService {
    public Point createCharge(User user, int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        return Point.createCharge(user, amount);
    }

    public int calculateBalance(List<Point> points){
        return points.stream()
                .filter(tx->!tx.isDeleted())
                .mapToInt(Point::getAmount)
                .sum();
    }
}
