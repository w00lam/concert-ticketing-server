package kr.hhplus.be.server.tokenqueue.infrastructure.persistence;

import kr.hhplus.be.server.tokenqueue.application.port.out.TokenQueueRepositoryPort;
import kr.hhplus.be.server.tokenqueue.domain.model.TokenQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TokenQueueRepositoryImpl implements TokenQueueRepositoryPort {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_KEY = "queue:token";

    @Override
    public void addUser(TokenQueue tokenQueue) {
        redisTemplate.opsForZSet().add(QUEUE_KEY, tokenQueue.getUserId(), tokenQueue.getJoinTimestamp());
    }

    @Override
    public void removeUser(String userId) {
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userId);
    }

    @Override
    public Integer getUserRank(String userId) {
        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, userId);
        return rank != null ? rank.intValue() + 1 : null; // 1-based index
    }

    @Override
    public Integer getQueueLength() {
        Long size = redisTemplate.opsForZSet().zCard(QUEUE_KEY);
        return size != null ? size.intValue() : 0;
    }

    @Override
    public String getNextUser() {
        Set<String> users = redisTemplate.opsForZSet().range(QUEUE_KEY, 0, 0);
        return (users != null && !users.isEmpty()) ? users.iterator().next() : null;
    }

    @Override
    public String popNextUser() {
        // ZPOPMIN performs read-and-remove in a single Redis command.
        ZSetOperations.TypedTuple<String> user = redisTemplate.opsForZSet().popMin(QUEUE_KEY);
        return user != null ? user.getValue() : null;
    }

    // =========================
    // 테스트용 전체 초기화
    // =========================
    public void clearQueue() {
        redisTemplate.delete(QUEUE_KEY);
    }
}
