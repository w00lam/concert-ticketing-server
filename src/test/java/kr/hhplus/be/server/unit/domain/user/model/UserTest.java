package kr.hhplus.be.server.unit.domain.user.model;

import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest extends BaseUnitTest {
    @Test
    @DisplayName("addPoints: 양수 금액 추가")
    void testAddPoints() {
        User user = userWithPoints(10);

        user.addPoints(5);
        assertEquals(15, user.getPoints());
    }

    @Test
    @DisplayName("addPoints: 음수 금액 예외")
    void testAddPointsNegative() {
        User user = userWithPoints(10);

        assertThrows(IllegalArgumentException.class, () -> user.addPoints(-5));
    }

    @Test
    @DisplayName("deductPoints: 충분한 포인트 차감")
    void testDeductPoints() {
        User user = userWithPoints(10);

        user.deductPoints(5);
        assertEquals(5, user.getPoints());
    }

    @Test
    @DisplayName("deductPoints: 음수 금액 예외")
    void testDeductPointsNegative() {
        User user = userWithPoints(10);

        assertThrows(IllegalArgumentException.class, () -> user.deductPoints(-5));
    }

    @Test
    @DisplayName("deductPoints: 부족한 포인트 예외")
    void testDeductPointsInsufficient() {
        User user = userWithPoints(5);

        assertThrows(IllegalStateException.class, () -> user.deductPoints(10));
    }

    private User userWithPoints(int points) {
        User user = User.create("test@example.com", "Test User");
        user.addPoints(points);
        return user;
    }
}
