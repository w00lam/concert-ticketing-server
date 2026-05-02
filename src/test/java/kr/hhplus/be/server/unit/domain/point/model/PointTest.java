package kr.hhplus.be.server.unit.domain.point.model;

import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.model.PointType;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PointTest extends BaseUnitTest {
    @Test
    @DisplayName("createCharge()는 충전 타입의 Point 엔티티를 생성해야 한다")
    void createCharge_shouldCreateChargePoint() {
        // given
        User user = User.builder()
                .id(fixedUUID())
                .build();

        int amount = 10000;

        // when
        Point point = Point.createCharge(user, amount);

        // then
        assertNull(point.getId(), "ID는 createCharge에서 생성되지 않는다");
        assertEquals(user, point.getUser());
        assertEquals(amount, point.getAmount());
        Assertions.assertEquals(PointType.CHARGE, point.getType());

        assertNull(point.getCreatedAt());
        assertNull(point.getUpdatedAt());

        assertFalse(point.isDeleted());
    }
}
