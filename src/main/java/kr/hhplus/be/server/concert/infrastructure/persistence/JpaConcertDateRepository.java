package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
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
public interface JpaConcertDateRepository extends JpaRepository<ConcertDate, UUID> {
    @Query("""
            select new kr.hhplus.be.server.concert.application.port.in.concertdate.GetConcertDatesResult(
                cd.id,
                cd.eventDate
            )
            from ConcertDate cd
            where cd.concert.id = :concertId
            """)
    List<GetConcertDatesResult> findDateResultsByConcertId(@Param("concertId") UUID concertId);
}
