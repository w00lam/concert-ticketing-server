package kr.hhplus.be.server.unit.domain.reservation.model;

import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.reservation.domain.model.DefaultReservationExpirationPolicy;
import kr.hhplus.be.server.reservation.domain.model.ReservationExpirationPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultReservationExpirationPolicyTest extends BaseUnitTest {
    @Test
    @DisplayName("예약 정책을 실행하면 5분의 시간이 추가되어야한다.")
     void testExpiresAtAddsFiveMinutes() {
        // given
        ReservationExpirationPolicy policy = new DefaultReservationExpirationPolicy();
        LocalDateTime baseTime = fixedNow();

        // when
        LocalDateTime expires = policy.expiresAt(baseTime);

        // then
        assertEquals(baseTime.plusMinutes(5), expires);
    }
}
