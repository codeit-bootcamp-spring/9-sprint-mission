package com.sprint.mission.discodeit.infrastructure.redis;

public interface RedisLockProvider {

  void acquireLock(String key);

  void releaseLock(String key);

  class RedisLockAcquisitionException extends RuntimeException {
    public RedisLockAcquisitionException(String message) {
      super(message);
    }
  }
}