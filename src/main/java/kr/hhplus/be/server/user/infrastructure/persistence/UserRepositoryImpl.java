package kr.hhplus.be.server.user.infrastructure.persistence;

import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {
    private final JpaUserRepository jpa;

    @Override
    public User save(User user) {
        return jpa.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return jpa.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
