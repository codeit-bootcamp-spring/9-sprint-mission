package com.sprint.mission.discodeit.redis;

import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.jwt.registry", havingValue = "redis", matchIfMissing = true)
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_KEY_PREFIX = "jwt:user:";
  private static final String ACCESS_KEY_PREFIX = "jwt:access:";
  private static final String REFRESH_KEY_PREFIX = "jwt:refresh:";
  private static final String LOCK_KEY_PREFIX = "jwt:";

  private final RedisTemplate<String, Object> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisLockProvider redisLockProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userDto().id();
    String lockKey = LOCK_KEY_PREFIX + userId;
    redisLockProvider.acquireLock(lockKey);
    try {
      invalidateJwtInformationByUserIdWithoutLock(userId);
      save(jwtInformation);
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    String lockKey = LOCK_KEY_PREFIX + userId;
    redisLockProvider.acquireLock(lockKey);
    try {
      invalidateJwtInformationByUserIdWithoutLock(userId);
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return findByUserId(userId)
        .filter(this::isActive)
        .isPresent();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return findByKey(accessKey(accessToken))
        .filter(this::isActive)
        .isPresent();
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return findByRefreshToken(refreshToken)
        .filter(this::isActive)
        .isPresent();
  }

  @Override
  public Optional<JwtInformation> findByRefreshToken(String refreshToken) {
    return findByKey(refreshKey(refreshToken));
  }

  @Override
  public JwtInformation rotateJwtInformation(String refreshToken, String newAccessToken,
      String newRefreshToken) {
    JwtInformation found = findByRefreshToken(refreshToken)
        .filter(this::isActive)
        .orElseThrow(() -> new IllegalArgumentException("Refresh token is not registered"));
    String lockKey = LOCK_KEY_PREFIX + found.userDto().id();
    redisLockProvider.acquireLock(lockKey);
    try {
      JwtInformation current = findByRefreshToken(refreshToken)
          .filter(this::isActive)
          .orElseThrow(() -> new IllegalArgumentException("Refresh token is not registered"));
      JwtInformation rotated = current.rotate(newAccessToken, newRefreshToken,
          jwtTokenProvider.getExpiresAt(newRefreshToken));

      delete(current);
      save(rotated);
      return rotated;
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    // Redis key TTL removes expired JWT information without scanning all keys.
  }

  private Optional<JwtInformation> findByUserId(UUID userId) {
    return findByKey(userKey(userId));
  }

  private Optional<JwtInformation> findByKey(String key) {
    Object value = redisTemplate.opsForValue().get(key);
    if (value instanceof JwtInformation jwtInformation) {
      return Optional.of(jwtInformation);
    }
    return Optional.empty();
  }

  private void save(JwtInformation jwtInformation) {
    Duration ttl = ttl(jwtInformation);
    if (ttl.isZero() || ttl.isNegative()) {
      return;
    }

    redisTemplate.opsForValue()
        .set(userKey(jwtInformation.userDto().id()), jwtInformation, ttl);
    redisTemplate.opsForValue()
        .set(accessKey(jwtInformation.accessToken()), jwtInformation, ttl);
    redisTemplate.opsForValue()
        .set(refreshKey(jwtInformation.refreshToken()), jwtInformation, ttl);
  }

  private void delete(JwtInformation jwtInformation) {
    redisTemplate.delete(userKey(jwtInformation.userDto().id()));
    redisTemplate.delete(accessKey(jwtInformation.accessToken()));
    redisTemplate.delete(refreshKey(jwtInformation.refreshToken()));
  }

  private void invalidateJwtInformationByUserIdWithoutLock(UUID userId) {
    findByUserId(userId).ifPresent(this::delete);
  }

  private Duration ttl(JwtInformation jwtInformation) {
    return Duration.between(Instant.now(), jwtInformation.refreshTokenExpiresAt());
  }

  private boolean isActive(JwtInformation jwtInformation) {
    return jwtInformation.refreshTokenExpiresAt().isAfter(Instant.now());
  }

  private String userKey(UUID userId) {
    return USER_KEY_PREFIX + userId;
  }

  private String accessKey(String accessToken) {
    return ACCESS_KEY_PREFIX + accessToken;
  }

  private String refreshKey(String refreshToken) {
    return REFRESH_KEY_PREFIX + refreshToken;
  }
}
