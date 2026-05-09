package kr.hhplus.be.server.unit.fixture;

import kr.hhplus.be.server.user.domain.model.User;

import java.util.UUID;

public final class UserFixture {
    private UserFixture() {
    }

    public static User user(UUID id, int points) {
        return User.builder()
                .id(id)
                .email("test-" + id + "@example.com")
                .name("Test User")
                .points(points)
                .deleted(false)
                .build();
    }
}
