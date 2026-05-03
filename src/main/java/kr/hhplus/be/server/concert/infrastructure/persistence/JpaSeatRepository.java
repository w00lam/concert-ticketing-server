package kr.hhplus.be.server.concert.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for concert entities.
 */

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByConcertDate_Id(UUID concertDateId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Seat> findByHoldUntilBeforeAndDeletedFalse(LocalDateTime now);
}
