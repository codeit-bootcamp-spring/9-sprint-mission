package com.sprint.mission.discodeit.redis;

import com.sprint.mission.discodeit.config.CacheConfig;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "discodeit.jwt.registry", havingValue = "redis", matchIfMissing = true)
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_KEY_PREFIX = "jwt:users:";
  private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
  private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
  private static final String LOCK_KEY_PREFIX = "jwt:";
  private static final int DEFAULT_MAX_ACTIVE_JWT_COUNT = 1;

  private final RedisTemplate<String, Object> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisLockProvider redisLockProvider;

  @CacheEvict(value = CacheConfig.USERS, allEntries = true)
  @Retryable(
      retryFor = RedisLockProvider.RedisLockAcquisitionException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2)
  )
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userDto().id();
    String lockKey = LOCK_KEY_PREFIX + userId;
    redisLockProvider.acquireLock(lockKey);
    try {
      removeExpiredJwtInformation(userId);
      Long currentSize = redisTemplate.opsForList().size(userKey(userId));
      while (currentSize != null && currentSize >= DEFAULT_MAX_ACTIVE_JWT_COUNT) {
        Object oldest = redisTemplate.opsForList().leftPop(userKey(userId));
        if (oldest instanceof JwtInformation oldestJwtInformation) {
          removeTokenIndex(oldestJwtInformation);
        }
        currentSize = redisTemplate.opsForList().size(userKey(userId));
      }
      save(jwtInformation);
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @CacheEvict(value = CacheConfig.USERS, allEntries = true)
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
    removeExpiredJwtInformation(userId);
    Long size = redisTemplate.opsForList().size(userKey(userId));
    return size != null && size > 0;
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return findByAccessToken(accessToken)
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
    Set<String> userKeys = redisTemplate.keys(USER_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return Optional.empty();
    }
    return userKeys.stream()
        .flatMap(userKey -> findAllByUserKey(userKey).stream())
        .filter(this::isActive)
        .filter(jwtInformation -> jwtInformation.refreshToken().equals(refreshToken))
        .findFirst();
  }

  @Retryable(
      retryFor = RedisLockProvider.RedisLockAcquisitionException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2)
  )
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

      removeFromUserList(current);
      removeTokenIndex(current);
      save(rotated);
      return rotated;
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Set<String> userKeys = redisTemplate.keys(USER_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return;
    }
    userKeys.forEach(userKey -> removeExpiredJwtInformation(extractUserId(userKey)));
  }

  private void save(JwtInformation jwtInformation) {
    Duration ttl = ttl(jwtInformation);
    if (ttl.isZero() || ttl.isNegative()) {
      return;
    }

    String userKey = userKey(jwtInformation.userDto().id());
    redisTemplate.opsForList().rightPush(userKey, jwtInformation);
    redisTemplate.expire(userKey, ttl);
    addTokenIndex(jwtInformation, ttl);
  }

  private void delete(JwtInformation jwtInformation) {
    removeFromUserList(jwtInformation);
    removeTokenIndex(jwtInformation);
  }

  private void invalidateJwtInformationByUserIdWithoutLock(UUID userId) {
    findAllByUserKey(userKey(userId)).forEach(this::removeTokenIndex);
    redisTemplate.delete(userKey(userId));
  }

  private List<JwtInformation> findAllByUserKey(String userKey) {
    List<Object> values = redisTemplate.opsForList().range(userKey, 0, -1);
    if (values == null || values.isEmpty()) {
      return List.of();
    }
    return values.stream()
        .filter(JwtInformation.class::isInstance)
        .map(JwtInformation.class::cast)
        .toList();
  }

  private Optional<JwtInformation> findByAccessToken(String accessToken) {
    Set<String> userKeys = redisTemplate.keys(USER_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return Optional.empty();
    }
    return userKeys.stream()
        .flatMap(userKey -> findAllByUserKey(userKey).stream())
        .filter(jwtInformation -> jwtInformation.accessToken().equals(accessToken))
        .findFirst();
  }

  private void removeFromUserList(JwtInformation jwtInformation) {
    redisTemplate.opsForList().remove(userKey(jwtInformation.userDto().id()), 1,
        jwtInformation);
  }

  private void removeExpiredJwtInformation(UUID userId) {
    List<JwtInformation> jwtInformations = findAllByUserKey(userKey(userId));
    jwtInformations.stream()
        .filter(jwtInformation -> !isActive(jwtInformation))
        .forEach(this::delete);
    if (findAllByUserKey(userKey(userId)).isEmpty()) {
      redisTemplate.delete(userKey(userId));
    }
  }

  private void addTokenIndex(JwtInformation jwtInformation, Duration ttl) {
    redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, jwtInformation.accessToken());
    redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, jwtInformation.refreshToken());
    redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, ttl);
    redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, ttl);
  }

  private void removeTokenIndex(JwtInformation jwtInformation) {
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, jwtInformation.accessToken());
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, jwtInformation.refreshToken());
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

  private UUID extractUserId(String userKey) {
    return UUID.fromString(userKey.substring(USER_KEY_PREFIX.length()));
  }
}
