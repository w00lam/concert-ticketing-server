package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
/**
 * Spring Data JPA repository for concert entities.
 */

@Repository
public interface JpaConcertDateRepository extends JpaRepository<ConcertDate, UUID> {
    List<ConcertDate> findAllByConcert_Id(UUID concertId);
}
