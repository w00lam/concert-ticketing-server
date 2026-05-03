package kr.hhplus.be.server.integration.tokenqueue;

import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
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
    void 준비_유저_생성() {
        // Reset the Redis queue directly in the test so production repository code stays focused on queue behavior.
        redisTemplate.delete(QUEUE_KEY);

        // 테스트 유저 생성
        user1 = createUser();
        user2 = createUser();
        user3 = createUser();
    }

    @Test
    void 유저_대기열_전체_흐름_검증() {
        // 1️⃣ 유저 추가
        tokenQueueUseCase.enqueueUser(user1.getId().toString());
        tokenQueueUseCase.enqueueUser(user2.getId().toString());
        tokenQueueUseCase.enqueueUser(user3.getId().toString());

        // 2️⃣ 대기열 길이 확인
        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(3);

        // 3️⃣ 순위 확인
        assertThat(tokenQueueUseCase.getUserRank(user1.getId().toString())).isEqualTo(1);
        assertThat(tokenQueueUseCase.getUserRank(user2.getId().toString())).isEqualTo(2);
        assertThat(tokenQueueUseCase.getUserRank(user3.getId().toString())).isEqualTo(3);

        // 4️⃣ 다음 유저 확인
        assertThat(tokenQueueUseCase.getNextUser()).isEqualTo(user1.getId().toString());

        // 5️⃣ dequeue 후 확인
        tokenQueueUseCase.dequeueUser();
        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(2);
        assertThat(tokenQueueUseCase.getNextUser()).isEqualTo(user2.getId().toString());

        // 6️⃣ 다시 순위 확인
        assertThat(tokenQueueUseCase.getUserRank(user2.getId().toString())).isEqualTo(1);
        assertThat(tokenQueueUseCase.getUserRank(user3.getId().toString())).isEqualTo(2);
    }
}
