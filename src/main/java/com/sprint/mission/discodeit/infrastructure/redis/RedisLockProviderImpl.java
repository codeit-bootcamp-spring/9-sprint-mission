package com.sprint.mission.discodeit.infrastructure.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisLockProviderImpl implements RedisLockProvider {

  private final StringRedisTemplate redisTemplate;

  private static final String PREFIX = "lock:";

  @Override
  public void acquireLock(String key) {
    Boolean success = redisTemplate.opsForValue()
        .setIfAbsent(PREFIX + key, "LOCK", Duration.ofSeconds(5));

    if (!Boolean.TRUE.equals(success)) {
      throw new RedisLockAcquisitionException("Failed to acquire lock: " + key);
    }
  }

  @Override
  public void releaseLock(String key) {
    redisTemplate.delete(PREFIX + key);
  }
}