package com.sprint.mission.discodeit.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockProvider {

  private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
  private static final String LOCK_KEY_PREFIX = "lock:";

  private final RedisTemplate<String, Object> redisTemplate;

  public void acquireLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
    ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
    Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

    if (Boolean.TRUE.equals(acquired)) {
      log.debug("Redis lock acquired: key={}, value={}", lockKey, lockValue);
      return;
    }
    throw new RedisLockAcquisitionException("Failed to acquire Redis lock: " + lockKey);
  }

  public void releaseLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    try {
      redisTemplate.delete(lockKey);
      log.debug("Redis lock released: key={}", lockKey);
    } catch (Exception exception) {
      log.warn("Failed to release Redis lock: key={}", lockKey, exception);
    }
  }

  public static class RedisLockAcquisitionException extends RuntimeException {

    public RedisLockAcquisitionException(String message) {
      super(message);
    }
  }
}
