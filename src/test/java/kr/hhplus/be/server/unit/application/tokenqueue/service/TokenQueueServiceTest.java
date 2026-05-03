package kr.hhplus.be.server.unit.application.tokenqueue.service;

import kr.hhplus.be.server.tokenqueue.application.port.in.TokenQueueUseCase;
import kr.hhplus.be.server.tokenqueue.application.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.tokenqueue.application.service.TokenQueueUseCaseImpl;
import kr.hhplus.be.server.tokenqueue.infrastructure.persistence.TokenQueueRepositoryImpl;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenQueueServiceTest extends BaseUnitTest {
    private TokenQueueUseCase tokenQueueService;
    private RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;

    @BeforeEach
    void setUp() {
        redisTemplate = Mockito.mock(RedisTemplate.class);
        zSetOperations = Mockito.mock(ZSetOperations.class);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        TokenQueueRepositoryPort repository = new TokenQueueRepositoryImpl(redisTemplate);
        tokenQueueService = new TokenQueueUseCaseImpl(repository);
    }

    @Test
    void enqueueUser_addsUserToRedisSortedSet() {
        String userId = fixedUUID().toString();

        tokenQueueService.enqueueUser(userId);

        verify(redisTemplate.opsForZSet(), times(1))
                .add(eq("queue:token"), eq(userId), anyDouble());
    }

    @Test
    void getQueueLength_returnsRedisSortedSetSize() {
        when(zSetOperations.zCard("queue:token")).thenReturn(3L);

        Integer length = tokenQueueService.getQueueLength();

        assertThat(length).isEqualTo(3);
    }

    @Test
    void getUserRank_returnsOneBasedRank() {
        when(zSetOperations.rank("queue:token", "user1")).thenReturn(0L);
        when(zSetOperations.rank("queue:token", "user2")).thenReturn(1L);

        Integer rank1 = tokenQueueService.getUserRank("user1");
        Integer rank2 = tokenQueueService.getUserRank("user2");

        assertThat(rank1).isEqualTo(1);
        assertThat(rank2).isEqualTo(2);
    }

    @Test
    void dequeueUser_popsFirstUserAtomically() {
        tokenQueueService.dequeueUser();

        // popMin maps to Redis ZPOPMIN, keeping admission atomic under concurrent servers.
        verify(redisTemplate.opsForZSet(), times(1)).popMin("queue:token");
    }

    @Test
    void getNextUser_peeksFirstUserWithoutRemovingIt() {
        when(zSetOperations.range("queue:token", 0, 0)).thenReturn(Set.of("user1"));

        String nextUser = tokenQueueService.getNextUser();

        assertThat(nextUser).isEqualTo("user1");
    }
}
