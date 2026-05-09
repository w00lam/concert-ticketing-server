package kr.hhplus.be.server.integration.reservation;

import kr.hhplus.be.server.concert.application.port.out.ConcertRankingRepositoryPort;
import kr.hhplus.be.server.concert.application.service.ConcertRankingItem;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertRankingRedisIntegrationTest extends ReservationIntegrationTestBase {
    @Autowired
    private ConcertRankingRepositoryPort concertRankingRepository;

    @Test
    void confirmedPaymentReflectsConcertRankingInRedis() {
        User user = createUserWithPoints(10_000);
        ReservedSeat reservedSeat = createReservedSeat(user, "ranking test concert");

        payReservation(reservedSeat.reservationId(), 5_000, PaymentMethod.CARD);

        List<ConcertRankingItem> rankings = concertRankingRepository.findTopRanked(10);

        assertThat(rankings)
                .extracting(ConcertRankingItem::concertId)
                .contains(reservedSeat.concertId());
    }
}
