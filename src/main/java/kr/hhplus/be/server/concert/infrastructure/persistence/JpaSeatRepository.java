package kr.hhplus.be.server.concert.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Seat s where s.id = :seatId and s.deleted = false")
    Optional<Seat> findByIdForUpdate(@Param("seatId") UUID seatId);

    List<Seat> findAllByConcertDate_Id(UUID concertDateId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findByHoldUntilBeforeAndDeletedFalse(LocalDateTime now);
}
