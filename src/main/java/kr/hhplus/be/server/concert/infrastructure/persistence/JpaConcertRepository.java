package kr.hhplus.be.server.concert.infrastructure.persistence;

import kr.hhplus.be.server.concert.domain.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaConcertRepository extends JpaRepository<Concert, UUID> {
}
