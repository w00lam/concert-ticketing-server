package kr.hhplus.be.server.integration.tokenqueue;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenQueueIntegrationTest extends ReservationIntegrationTestBase {
    private static final String QUEUE_KEY = "queue:token";

    @Autowired
    private TokenQueueUseCase tokenQueueUseCase;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUpQueueUsers() {
        redisTemplate.delete(QUEUE_KEY);

        user1 = createUser();
        user2 = createUser();
        user3 = createUser();
    }

    @Test
    @DisplayName("Redis 대기열은 입력 순서대로 순번을 부여하고 첫 사용자를 원자적으로 제거한다")
    void tokenQueuePreservesOrderAndDequeuesFirstUser() {
        String userId1 = user1.getId().toString();
        String userId2 = user2.getId().toString();
        String userId3 = user3.getId().toString();

        tokenQueueUseCase.enqueueUser(userId1);
        tokenQueueUseCase.enqueueUser(userId2);
        tokenQueueUseCase.enqueueUser(userId3);

        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(3);
        assertThat(tokenQueueUseCase.getUserRank(userId1)).isEqualTo(1);
        assertThat(tokenQueueUseCase.getUserRank(userId2)).isEqualTo(2);
        assertThat(tokenQueueUseCase.getUserRank(userId3)).isEqualTo(3);
        assertThat(tokenQueueUseCase.getNextUser()).isEqualTo(userId1);

        tokenQueueUseCase.dequeueUser();

        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(2);
        assertThat(tokenQueueUseCase.getUserRank(userId1)).isNull();
        assertThat(tokenQueueUseCase.getUserRank(userId2)).isEqualTo(1);
        assertThat(tokenQueueUseCase.getUserRank(userId3)).isEqualTo(2);
        assertThat(tokenQueueUseCase.getNextUser()).isEqualTo(userId2);
    }
}
