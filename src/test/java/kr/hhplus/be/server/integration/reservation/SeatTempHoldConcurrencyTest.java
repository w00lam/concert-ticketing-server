package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class SeatTempHoldConcurrencyTest extends ReservationIntegrationTestBase {
    @Test
    @DisplayName("동일 좌석에 대해 동시에 예약 요청 시 임시 배정은 1건만 성공한다")
    void reserve_seat_concurrently() throws Exception {
        User user1 = createUser();
        User user2 = createUser();
        User user3 = createUser();
        Concert concert = Concert.create("concert");

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(
                        concertRepository.save(concert),
                        LocalDate.now()
                )
        );

        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");
        UUID seatId = seat.getId();
        List<User> users = List.of(user1, user2, user3);
        int threadCount = users.size();

        var result = ConcurrencyTestSupport.runConcurrently(threadCount, index -> {
            try {
                reserveSeat(users.get(index).getId(), concert.getId(), seatId);
                return true;
            } catch (Exception exception) {
                return false;
            }
        });

        assertThat(result.failures()).isEmpty();
        assertThat(result.matchingSuccessCount(Boolean::booleanValue)).isEqualTo(1);
        assertThat(countReservationsBySeatAndStatus(seat, ReservationStatus.TEMP_HOLD)).isEqualTo(1);
    }
}
