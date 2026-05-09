package kr.hhplus.be.server.user.infrastructure.persistence;

import kr.hhplus.be.server.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
/**
 * Spring Data JPA repository for user entities.
 */

public interface JpaUserRepository extends JpaRepository<User, UUID> {
}
