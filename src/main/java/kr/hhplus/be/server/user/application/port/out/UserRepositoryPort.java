package kr.hhplus.be.server.user.application.port.out;

import kr.hhplus.be.server.user.domain.model.User;

import java.util.UUID;
/**
 * Defines the output port for user persistence.
 */

public interface UserRepositoryPort {
    User save(User user);
    User findById(UUID userId);
}
