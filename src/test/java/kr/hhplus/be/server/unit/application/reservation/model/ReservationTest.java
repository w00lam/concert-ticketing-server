package kr.hhplus.be.server.unit.application.reservation.model;

import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.unit.fixture.ReservationFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ReservationTest extends BaseUnitTest {
    @Test
    void 예약을_생성하면_TEMP_HOLD_상태로_생성된다() {
        Reservation reservation = ReservationFixture.tempHold(fixedNow().plusMinutes(10));

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMP_HOLD);
        assertThat(reservation.getTempHoldExpiresAt()).isAfter(fixedNow());
    }

    @Test
    void 예약을_취소하면_상태가_CANCELED가_되고_deleted가_true가_된다() {
        Reservation reservation = ReservationFixture.tempHold(fixedUUID());

        // when
        reservation.cancel();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
        assertThat(reservation.isDeleted()).isTrue();
    }

    @Test
    void 이미_취소된_예약을_다시_취소하면_예외가_발생한다() {
        Reservation reservation = ReservationFixture.canceled();

        // when & then
        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 취소된 예약입니다.");
    }
}
