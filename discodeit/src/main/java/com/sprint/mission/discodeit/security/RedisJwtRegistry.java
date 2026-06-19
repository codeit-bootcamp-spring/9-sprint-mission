package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "discodeit.security.jwt-registry",
    name = "type",
    havingValue = "redis"
)
@Component
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
  private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
  private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisLockProvider redisLockProvider;

  @Value("${discodeit.security.token.max-active-count:1}")
  private int maxActiveJwtCount;

  @Value("${discodeit.security.token.refresh-expiration-seconds:604800}")
  private long refreshExpirationSeconds;

  @Retryable(
      retryFor = RedisLockAcquisitionException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2)
  )
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    validateMaxActiveJwtCount();
    String userKey = getUserKey(jwtInformation.userId());
    String lockKey = jwtInformation.userId().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      removeExpired(userKey, Instant.now());
      Long currentSize = redisTemplate.opsForList().size(userKey);
      while (currentSize != null && currentSize >= maxActiveJwtCount) {
        Object oldestJwtInformation = redisTemplate.opsForList().leftPop(userKey);
        if (oldestJwtInformation instanceof JwtInformation jwt) {
          removeTokenIndex(jwt);
        }
        currentSize = redisTemplate.opsForList().size(userKey);
      }

      redisTemplate.opsForList().rightPush(userKey, jwtInformation);
      expireUserKey(userKey);
      addTokenIndex(jwtInformation);
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);
    findAll(userKey).forEach(this::removeTokenIndex);
    redisTemplate.delete(userKey);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }
    Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return;
    }

    userKeys.forEach(userKey -> removeIf(userKey,
        jwtInformation -> refreshToken.equals(jwtInformation.refreshToken())));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);
    removeExpired(userKey, Instant.now());
    Long size = redisTemplate.opsForList().size(userKey);
    return size != null && size > 0;
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    if (accessToken == null || accessToken.isBlank()) {
      return false;
    }
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
    );
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return false;
    }
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
    );
  }

  @Retryable(
      retryFor = RedisLockAcquisitionException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2)
  )
  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation rotatedJwtInformation) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }
    String userKey = getUserKey(rotatedJwtInformation.userId());
    String lockKey = rotatedJwtInformation.userId().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      List<JwtInformation> jwtInformations = findAll(userKey);
      for (int i = 0; i < jwtInformations.size(); i++) {
        JwtInformation jwtInformation = jwtInformations.get(i);
        if (refreshToken.equals(jwtInformation.refreshToken())) {
          removeTokenIndex(jwtInformation);
          redisTemplate.opsForList().set(userKey, i, rotatedJwtInformation);
          addTokenIndex(rotatedJwtInformation);
          expireUserKey(userKey);
          return;
        }
      }
    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Set<String> userKeys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (userKeys == null || userKeys.isEmpty()) {
      return;
    }
    Instant now = Instant.now();
    userKeys.forEach(userKey -> removeIf(userKey, jwtInformation -> jwtInformation.isExpired(now)));
  }

  private void removeExpired(String userKey, Instant now) {
    removeIf(userKey, jwtInformation -> jwtInformation.isExpired(now));
  }

  private void removeIf(String userKey, JwtInformationMatcher matcher) {
    List<JwtInformation> jwtInformations = findAll(userKey);
    jwtInformations.stream()
        .filter(matcher::matches)
        .forEach(jwtInformation -> {
          redisTemplate.opsForList().remove(userKey, 1, jwtInformation);
          removeTokenIndex(jwtInformation);
        });

    Long size = redisTemplate.opsForList().size(userKey);
    if (size != null && size == 0) {
      redisTemplate.delete(userKey);
    }
  }

  private List<JwtInformation> findAll(String userKey) {
    List<Object> values = redisTemplate.opsForList().range(userKey, 0, -1);
    if (values == null || values.isEmpty()) {
      return List.of();
    }
    return values.stream()
        .filter(JwtInformation.class::isInstance)
        .map(JwtInformation.class::cast)
        .toList();
  }

  private String getUserKey(UUID userId) {
    return USER_JWT_KEY_PREFIX + userId;
  }

  private void addTokenIndex(JwtInformation jwtInformation) {
    redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, jwtInformation.accessToken());
    redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, jwtInformation.refreshToken());
    Duration ttl = Duration.ofSeconds(refreshExpirationSeconds);
    redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, ttl);
    redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, ttl);
  }

  private void removeTokenIndex(JwtInformation jwtInformation) {
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, jwtInformation.accessToken());
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, jwtInformation.refreshToken());
  }

  private void expireUserKey(String userKey) {
    redisTemplate.expire(userKey, Duration.ofSeconds(refreshExpirationSeconds));
  }

  private void validateMaxActiveJwtCount() {
    if (maxActiveJwtCount < 1) {
      throw new IllegalArgumentException("maxActiveJwtCount must be greater than 0");
    }
  }

  @FunctionalInterface
  private interface JwtInformationMatcher {

    boolean matches(JwtInformation jwtInformation);
  }
}
