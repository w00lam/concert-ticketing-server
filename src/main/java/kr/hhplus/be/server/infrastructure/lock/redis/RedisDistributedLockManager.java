package kr.hhplus.be.server.infrastructure.lock.redis;

import kr.hhplus.be.server.infrastructure.lock.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
/**
 * Uses Redis to acquire and safely release distributed locks.
 */

@Component
@RequiredArgsConstructor
public class RedisDistributedLockManager implements DistributedLockManager {
    private final RedisTemplate<String, String> redisTemplate;
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            end
            return 0
            """,
            Long.class
    );


    @Override
    public String lock(String key, Duration ttl) {
        String lockValue = UUID.randomUUID().toString();

        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, lockValue, ttl);

        if(Boolean.TRUE.equals(success)){
            return lockValue;
        }
        return null;
    }

    @Override
    public void unlock(String key, String lockValue) {
        // Compare-and-delete is atomic, so an expired old owner cannot delete a new owner's lock.
        redisTemplate.execute(UNLOCK_SCRIPT, List.of(key), lockValue);
    }
}
