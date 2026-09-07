package kr.hhplus.be.server.integration.tokenqueue;

import kr.hhplus.be.server.integration.ReservationIntegrationTestBase;
import kr.hhplus.be.server.integration.support.ConcurrencyTestSupport;
import kr.hhplus.be.server.tokenqueue.application.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenQueueIntegrationTest extends ReservationIntegrationTestBase {
    private static final String QUEUE_KEY = "queue:token";
    private static final int QUEUE_SIZE = 1_000;
    private static final int DEQUEUE_WORKERS = 8;

    @Autowired
    private TokenQueueUseCase tokenQueueUseCase;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TokenQueueRepositoryPort tokenQueueRepository;

    private List<User> users;

    @BeforeEach
    void setUpQueueUsers() {
        redisTemplate.delete(QUEUE_KEY);

        users = IntStream.range(0, QUEUE_SIZE)
                .mapToObj(index -> createUser())
                .toList();
    }

    @Test
    @DisplayName("Redis 대기열은 1,000명의 순번을 관리하고 동시 dequeue에서 사용자를 중복 입장시키지 않는다")
    void tokenQueuePreservesRanksAndDequeuesEachUserOnce() throws Exception {
        List<String> userIds = users.stream()
                .map(user -> user.getId().toString())
                .toList();

        userIds.forEach(tokenQueueUseCase::enqueueUser);

        assertThat(tokenQueueUseCase.getQueueLength()).isEqualTo(QUEUE_SIZE);
        List<Integer> ranks = userIds.stream()
                .map(tokenQueueUseCase::getUserRank)
                .toList();
        assertThat(ranks).containsExactlyInAnyOrderElementsOf(
                IntStream.rangeClosed(1, QUEUE_SIZE).boxed().toList()
        );

        String firstUser = userIds.stream()
                .filter(userId -> tokenQueueUseCase.getUserRank(userId).equals(1))
                .findFirst()
                .orElseThrow();
        assertThat(tokenQueueUseCase.getNextUser()).isEqualTo(firstUser);

        long startedAt = System.nanoTime();
        var result = ConcurrencyTestSupport.runConcurrently(DEQUEUE_WORKERS, workerIndex -> {
            List<String> poppedUsers = new ArrayList<>();
            for (int index = workerIndex; index < QUEUE_SIZE; index += DEQUEUE_WORKERS) {
                String poppedUser = tokenQueueRepository.popNextUser();
                if (poppedUser == null) {
                    throw new IllegalStateException("Expected a queued user for dequeue index " + index);
                }
                poppedUsers.add(poppedUser);
            }
            return poppedUsers;
        });
        long elapsedNanos = System.nanoTime() - startedAt;

        List<String> poppedUsers = result.flatMapSuccesses(List::stream);
        assertThat(result.failures()).isEmpty();
        assertThat(poppedUsers).hasSize(QUEUE_SIZE);
        assertThat(poppedUsers.stream().distinct()).hasSize(QUEUE_SIZE);
        assertThat(poppedUsers).containsExactlyInAnyOrderElementsOf(userIds);
        assertThat(tokenQueueUseCase.getQueueLength()).isZero();

        double throughput = QUEUE_SIZE / (elapsedNanos / 1_000_000_000.0);
        System.out.printf(
                Locale.ROOT,
                "queue_dequeue_users=%d, dequeue_workers=%d, elapsed_ms=%d, throughput_per_second=%.2f%n",
                QUEUE_SIZE,
                DEQUEUE_WORKERS,
                elapsedNanos / 1_000_000,
                throughput
        );
    }
}
