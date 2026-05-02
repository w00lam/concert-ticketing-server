package kr.hhplus.be.server.unit.domain.point.service;

import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.model.PointType;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointDomainServiceTest extends BaseUnitTest {
    private final PointDomainService service = new PointDomainService();

    @Test
    @DisplayName("createCharge: 양수 금액은 정상 생성")
    void createCharge_valid() {
        User user = User.builder().id(fixedUUID()).build();
        Point point = service.createCharge(user, 1000);

        assertNotNull(point);
        assertEquals(user, point.getUser());
        assertEquals(1000, point.getAmount());
        assertEquals(PointType.CHARGE, point.getType());
        assertFalse(point.isDeleted());
    }

    @Test
    @DisplayName("createCharge: 0 이하 금액은 예외 발생")
    void createCharge_invalid() {
        User user = User.builder().id(fixedUUID()).build();

        assertThrows(IllegalArgumentException.class, () -> service.createCharge(user, 0));
        assertThrows(IllegalArgumentException.class, () -> service.createCharge(user, -100));
    }

    @Test
    @DisplayName("calculateBalance: 삭제되지 않은 포인트 합산")
    void calculateBalance_sum() {
        User user = User.builder().id(fixedUUID()).build();

        Point p1 = Point.builder().user(user).amount(1000).type(PointType.CHARGE).deleted(false).build();
        Point p2 = Point.builder().user(user).amount(500).type(PointType.CHARGE).deleted(true).build(); // 삭제된 포인트
        Point p3 = Point.builder().user(user).amount(200).type(PointType.CHARGE).deleted(false).build();

        int balance = service.calculateBalance(List.of(p1, p2, p3));
        assertEquals(1200, balance); // 1000 + 200
    }
}
