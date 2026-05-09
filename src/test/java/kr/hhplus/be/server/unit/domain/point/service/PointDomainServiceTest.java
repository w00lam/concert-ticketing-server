package kr.hhplus.be.server.unit.domain.point.service;

import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.model.PointType;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.unit.fixture.UserFixture;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointDomainServiceTest extends BaseUnitTest {
    private final PointDomainService service = new PointDomainService();

    @Test
    @DisplayName("charge: 양수 금액은 충전 기록을 만들고 유저 포인트를 증가시킨다")
    void charge_valid() {
        User user = User.create("point-service@example.com", "point tester");

        Point point = service.charge(user, 1000);

        assertNotNull(point);
        assertEquals(user, point.getUser());
        assertEquals(1000, point.getAmount());
        assertEquals(PointType.CHARGE, point.getType());
        assertFalse(point.isDeleted());
        assertEquals(1000, user.getPoints());
    }

    @Test
    @DisplayName("charge: 0 이하 금액은 예외가 발생한다")
    void charge_invalid() {
        User user = User.create("point-service-invalid@example.com", "point tester");

        assertThrows(IllegalArgumentException.class, () -> service.charge(user, 0));
        assertThrows(IllegalArgumentException.class, () -> service.charge(user, -100));
    }

    @Test
    @DisplayName("deduct: 유저 포인트를 차감한다")
    void deduct_valid() {
        User user = UserFixture.user(fixedUUID(), 1000);

        service.deduct(user, 400);

        assertEquals(600, user.getPoints());
    }
}
