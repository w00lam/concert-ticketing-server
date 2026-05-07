package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertRankingRedisIntegrationTest extends ReservationIntegrationTestBase {
    @Autowired
    private ConcertRankingRepositoryPort concertRankingRepository;

    @Test
    void 예약이_결제로_확정되면_콘서트_랭킹이_Redis에_반영된다() {
        // given
        User user = createUserWithPoints(10_000);

        Concert concert = concertRepository.save(
                Concert.builder()
                        .title("랭킹 테스트 콘서트")
                        .build()
        );

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );

        Seat seat = createSeatWithConcert(
                concertDate,
                "A", "1", "1", "VIP"
        );

        // 예약
        UUID reservationId = reserveSeat(
                user.getId(),
                concert.getId(),
                seat.getId()
        ).reservationId();

        // when: 결제 → 예약 확정
        payReservation(reservationId, 5_000, PaymentMethod.CARD);

        // then: 콘서트 랭킹에 반영됨
        List<ConcertRankingItem> rankings = concertRankingRepository.findTopRanked(10);

        assertThat(rankings)
                .extracting(ConcertRankingItem::concertId)
                .contains(concert.getId());
    }
}
