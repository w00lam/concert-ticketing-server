package kr.hhplus.be.server.point.domain.service;

import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointDomainService {
    public Point createCharge(User user, int amount) {
        if (amount <= 0) throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_POSITIVE, "금액은 0보다 커야 합니다.");
        return Point.createCharge(user, amount);
    }

    public int calculateBalance(List<Point> points) {
        return points.stream()
                .filter(tx -> !tx.isDeleted())
                .mapToInt(Point::getAmount)
                .sum();
    }
}
