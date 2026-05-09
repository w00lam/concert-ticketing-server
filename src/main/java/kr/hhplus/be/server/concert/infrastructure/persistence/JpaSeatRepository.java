package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult;
import kr.hhplus.be.server.concert.domain.model.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for concert entities.
 */

@Repository
public interface JpaSeatRepository extends JpaRepository<Seat, UUID> {
    @Query("""
            select new kr.hhplus.be.server.concert.application.port.in.seat.GetSeatsResult(
                s.id,
                s.section,
                s.row,
                s.number,
                s.grade
            )
            from Seat s
            where s.concertDate.id = :concertDateId
            """)
    List<GetSeatsResult> findSeatResultsByConcertDateId(@Param("concertDateId") UUID concertDateId);
}
