package kr.hhplus.be.server.infrastructure.lock;

import java.time.Duration;
/**
 * Coordinates exclusive access to shared resources in a distributed environment.
 */

public interface DistributedLockManager {
    /**
     * @return lockValue(UUID) or null if failed
     */
    String lock(String key, Duration ttl);
    void unlock(String key,String lockValue);
}
