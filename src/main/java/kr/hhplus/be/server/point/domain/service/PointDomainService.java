package kr.hhplus.be.server.point.domain.service;

import kr.hhplus.be.server.common.domain.AmountValidator;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Encapsulates domain rules for the point feature.
 */

@Component
public class PointDomainService {
    public Point charge(User user, int amount) {
        AmountValidator.requirePositive(amount);
        user.addPoints(amount);
        return Point.createCharge(user, amount);
    }

    public void deduct(User user, int amount) {
        user.deductPoints(amount);
    }
}
