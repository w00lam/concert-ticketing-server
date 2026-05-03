package kr.hhplus.be.server.user.infrastructure.persistence;

import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.exception.ResourceNotFoundException;
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
        return jpa.findById(userId).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));
    }
}
