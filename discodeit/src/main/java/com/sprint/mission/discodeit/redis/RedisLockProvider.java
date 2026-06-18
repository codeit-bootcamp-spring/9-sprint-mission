package com.sprint.mission.discodeit.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisLockProvider {

  private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
  private static final String LOCK_KEY_PREFIX = "lock:";

  private final RedisTemplate<String, Object> redisTemplate;

  public void acquireLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    String lockValue = Thread.currentThread().getName() + "-" + System.currentTimeMillis();
    ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();

    // SETNX: 키가 없으면 설정하고 TTL 지정
    Boolean acquired = valueOps.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT);

    if (Boolean.TRUE.equals(acquired)) {
      log.debug("분산 락 획득 성공: {} (값: {})", lockKey, lockValue);
    } else {
      log.debug("분산 락 획득 실패: {}", lockKey);
      throw new RedisLockAcquisitionException("분산 락 획득 실패: " + lockKey);
    }
  }

  public void releaseLock(String key) {
    String lockKey = LOCK_KEY_PREFIX + key;
    try {
      redisTemplate.delete(lockKey);
      log.debug("분산 락 해제 완료: {}", lockKey);
    } catch (Exception e) {
      log.warn("분산 락 해제 실패: {}", lockKey, e);
    }
  }

  public static class RedisLockAcquisitionException extends RuntimeException {

    public RedisLockAcquisitionException(String message) {
      super(message);
    }
  }
}
