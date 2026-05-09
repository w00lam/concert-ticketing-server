package kr.hhplus.be.server.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.concert.infrastructure.persistence.ConcertDateRepositoryImpl;
import kr.hhplus.be.server.concert.infrastructure.persistence.ConcertRepositoryImpl;
import kr.hhplus.be.server.concert.infrastructure.persistence.SeatRepositoryImpl;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.payment.infrastructure.persistence.PaymentRepositoryImpl;
import kr.hhplus.be.server.point.infrastructure.persistence.PointRepositoryImpl;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.reservation.infrastructure.persistence.ReservationRepositoryImpl;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.persistence.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersConfiguration.Initializer.class)
public abstract class ReservationIntegrationTestBase {
    @Autowired
    protected MakeReservationUseCase makeReservationUseCase;

    @Autowired
    protected MakePaymentUseCase makePaymentUseCase;

    @Autowired
    protected UserRepositoryImpl userRepository;

    @Autowired
    protected ReservationRepositoryImpl reservationRepository;

    @Autowired
    protected PointRepositoryImpl pointRepository;

    @Autowired
    protected PaymentRepositoryImpl paymentRepository;

    @Autowired
    protected ConcertRepositoryImpl concertRepository;

    @Autowired
    protected ConcertDateRepositoryImpl concertDateRepository;

    @Autowired
    protected SeatRepositoryImpl seatRepository;

    @PersistenceContext
    protected EntityManager em;

    protected User createUser() {
        User user = User.create("test-user-" + UUID.randomUUID() + "@example.com", "nickname");
        return userRepository.save(user);
    }

    protected User createUserWithPoints(int points) {
        User user = User.create("test-user-" + UUID.randomUUID() + "@example.com", "tester");
        user.addPoints(points);
        return userRepository.save(user);
    }

    protected Seat createSeat() {
        Concert concert = concertRepository.save(
                Concert.create("test concert")
        );
        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );
        Seat seat = Seat.create(concertDate, "A", "1", "1", "VIP");

        return seatRepository.save(seat);
    }

    protected Seat createSeatWithConcert(ConcertDate concertDate, String section, String row, String number, String grade) {
        Seat seat = Seat.create(concertDate, section, row, number, grade);
        return seatRepository.save(seat);
    }

    protected ReservedSeat createReservedSeat(User user, String concertTitle) {
        Concert concert = concertRepository.save(
                Concert.create(concertTitle)
        );
        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );
        Seat seat = createSeatWithConcert(concertDate, "A", "1", "1", "VIP");
        UUID reservationId = reserveSeat(user.getId(), concert.getId(), seat.getId()).reservationId();

        return new ReservedSeat(reservationId, concert.getId(), seat.getId());
    }

    protected UUID createReservedSeatId(User user, String concertTitle) {
        return createReservedSeat(user, concertTitle).reservationId();
    }

    protected MakeReservationResult reserveSeat(UUID userId, UUID concertId, UUID seatId) {
        MakeReservationCommand command = new MakeReservationCommand(userId, concertId, seatId);
        return makeReservationUseCase.execute(command);
    }

    protected MakePaymentResult payReservation(UUID reservationId, int amount, PaymentMethod method) {
        MakePaymentCommand command = new MakePaymentCommand(reservationId, amount, method);
        return makePaymentUseCase.execute(command);
    }

    protected MakePaymentCommand cardPaymentCommand(UUID reservationId, int amount) {
        return new MakePaymentCommand(reservationId, amount, PaymentMethod.CARD);
    }

    protected long countReservationsBySeatAndStatus(Seat seat, ReservationStatus status) {
        return em.createQuery("""
                        SELECT COUNT(r)
                        FROM Reservation r
                        WHERE r.seat = :seat
                        AND r.status = :status
                        """, Long.class)
                .setParameter("seat", seat)
                .setParameter("status", status)
                .getSingleResult();
    }

    protected long countPaymentsByReservationId(UUID reservationId) {
        return em.createQuery("""
                        SELECT COUNT(p)
                        FROM Payment p
                        WHERE p.reservation.id = :reservationId
                        """, Long.class)
                .setParameter("reservationId", reservationId)
                .getSingleResult();
    }

    protected record ReservedSeat(UUID reservationId, UUID concertId, UUID seatId) {
    }
}
