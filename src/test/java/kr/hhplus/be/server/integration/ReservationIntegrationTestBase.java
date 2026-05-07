package kr.hhplus.be.server.integration;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentCommand;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentResult;
import kr.hhplus.be.server.payment.application.port.in.MakePaymentUseCase;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationResult;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.TestcontainersConfiguration;
import kr.hhplus.be.server.concert.domain.model.Concert;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import kr.hhplus.be.server.payment.domain.model.PaymentMethod;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.user.domain.model.User;

import kr.hhplus.be.server.concert.infrastructure.persistence.ConcertDateRepositoryImpl;
import kr.hhplus.be.server.concert.infrastructure.persistence.ConcertRepositoryImpl;
import kr.hhplus.be.server.concert.infrastructure.persistence.SeatRepositoryImpl;
import kr.hhplus.be.server.payment.infrastructure.persistence.PaymentRepositoryImpl;
import kr.hhplus.be.server.point.infrastructure.persistence.PointRepositoryImpl;
import kr.hhplus.be.server.reservation.infrastructure.persistence.ReservationRepositoryImpl;
import kr.hhplus.be.server.user.infrastructure.persistence.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersConfiguration.Initializer.class)
public abstract class ReservationIntegrationTestBase {
    /*
     * ================
     *   USE CASES
     * ================
     */
    @Autowired
    protected MakeReservationUseCase makeReservationUseCase;

    @Autowired
    protected MakePaymentUseCase makePaymentUseCase;

    /*
     * =========================
     *     INFRA REPOSITORIES
     * =========================
     */
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

    /*
     * =========================
     *    HELPER: CREATE USER
     * =========================
     */
    protected User createUser() {
        User user = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@example.com")
                .name("nickname")
                .points(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();


        User saved = userRepository.save(user);

        return saved;
    }

    protected User createUserWithPoints(int points) {
        User user = User.builder()
                .email("test-user-" + UUID.randomUUID() + "@example.com")
                .name("tester")
                .points(points)
                .deleted(false)
                .build();

        User saved = userRepository.save(user);

        return saved;
    }

    /*
     * =========================
     *    HELPER: CREATE Seat
     * =========================
     */
    protected Seat createSeat() {
        Concert concert = concertRepository.save(
                Concert.builder()
                        .title("테스트 콘서트")
                        .build()
        );

        ConcertDate concertDate = concertDateRepository.save(
                ConcertDate.create(concert, LocalDate.now())
        );

        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .section("A")
                .row("1")
                .number("1")
                .grade("VIP")
                .deleted(false)
                .build();

        Seat saved = seatRepository.save(seat);

        return saved;
    }

    protected Seat createSeatWithConcert(ConcertDate concertDate, String section, String row, String number, String grade) {
        Seat seat = Seat.builder()
                .concertDate(concertDate)
                .section(section)
                .row(row)
                .number(number)
                .grade(grade)
                .build();
        Seat saved = seatRepository.save(seat);

        return saved;
    }

    /*
     * =============================================
     *     HELPER: MAKE RESERVATION → RETURN RESULT
     * =============================================
     */
    protected MakeReservationResult reserveSeat(UUID userId, UUID concertId,UUID seatId) {
        MakeReservationCommand cmd = new MakeReservationCommand(userId, concertId, seatId);
        return makeReservationUseCase.execute(cmd);
    }

    /*
     * =============================================
     *     HELPER: MAKE PAYMENT → RETURN RESULT
     * =============================================
     */
    protected MakePaymentResult payReservation(UUID reservationId, int amount, PaymentMethod method) {
        MakePaymentCommand cmd = new MakePaymentCommand(reservationId, amount, method);
        return makePaymentUseCase.execute(cmd);
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
}
