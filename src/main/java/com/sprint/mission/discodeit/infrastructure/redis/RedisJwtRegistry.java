package com.sprint.mission.discodeit.infrastructure.redis;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
  private static final String ACCESS_TOKEN_INDEX_KEY = "jwt:access_tokens";
  private static final String REFRESH_TOKEN_INDEX_KEY = "jwt:refresh_tokens";
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;
  private final ApplicationEventPublisher eventPublisher;
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisLockProvider redisLockProvider;

  private String userKey(UUID userId) {
    return USER_JWT_KEY_PREFIX + userId;
  }

  @CacheEvict(value = "users", key = "'all'")
  @Retryable(
      retryFor = RedisLockProvider.RedisLockAcquisitionException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2)
  )
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {

    String userKey = userKey(jwtInformation.getUserDto().id());
    String lockKey = jwtInformation.getUserDto().id().toString();

    redisLockProvider.acquireLock(lockKey);

    try {
      while (Boolean.TRUE.equals(redisTemplate.hasKey(userKey))
          && redisTemplate.opsForList().size(userKey) >= maxActiveJwtCount) {

        Object old = redisTemplate.opsForList().leftPop(userKey);

        if (old instanceof JwtInformation jwt) {
          removeTokenIndex(jwt.getAccessToken(), jwt.getRefreshToken());
        }
      }

      redisTemplate.opsForList().rightPush(userKey, jwtInformation);
      redisTemplate.expire(userKey, DEFAULT_TTL);

      addTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());

    } finally {
      redisLockProvider.releaseLock(lockKey);
    }

    eventPublisher.publishEvent(
        new UserLogInOutEvent(jwtInformation.getUserDto().id(), true)
    );
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {

    String key = userKey(userId);

    List<Object> list = redisTemplate.opsForList().range(key, 0, -1);

    if (list != null) {
      for (Object obj : list) {
        if (obj instanceof JwtInformation jwt) {
          removeTokenIndex(jwt.getAccessToken(), jwt.getRefreshToken());
        }
      }
    }

    redisTemplate.delete(key);

    eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    String key = userKey(userId);
    Long size = redisTemplate.opsForList().size(key);
    return size != null && size > 0;
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(ACCESS_TOKEN_INDEX_KEY, accessToken)
    );
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return Boolean.TRUE.equals(
        redisTemplate.opsForSet().isMember(REFRESH_TOKEN_INDEX_KEY, refreshToken)
    );
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwt) {

    String key = userKey(newJwt.getUserDto().id());
    String lockKey = newJwt.getUserDto().id().toString();

    redisLockProvider.acquireLock(lockKey);

    try {
      List<Object> list = redisTemplate.opsForList().range(key, 0, -1);

      if (list != null) {
        for (int i = 0; i < list.size(); i++) {

          Object obj = list.get(i);

          if (obj instanceof JwtInformation jwt
              && jwt.getRefreshToken().equals(refreshToken)) {

            removeTokenIndex(jwt.getAccessToken(), jwt.getRefreshToken());

            jwt.rotate(
                newJwt.getAccessToken(),
                newJwt.getRefreshToken()
            );

            redisTemplate.opsForList().set(key, i, jwt);
            addTokenIndex(newJwt.getAccessToken(), newJwt.getRefreshToken());

            redisTemplate.expire(key, DEFAULT_TTL);
            break;
          }
        }
      }

    } finally {
      redisLockProvider.releaseLock(lockKey);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void clearExpiredJwtInformation() {

    Set<String> keys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (keys == null) return;

    for (String key : keys) {

      List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
      if (list == null) continue;

      for (Object obj : list) {

        if (obj instanceof JwtInformation jwt) {

          boolean expired =
              !jwtTokenProvider.validateAccessToken(jwt.getAccessToken())
                  || !jwtTokenProvider.validateRefreshToken(jwt.getRefreshToken());

          if (expired) {
            redisTemplate.opsForList().remove(key, 1, jwt);
            removeTokenIndex(jwt.getAccessToken(), jwt.getRefreshToken());
          }
        }
      }

      if (redisTemplate.opsForList().size(key) == 0) {
        redisTemplate.delete(key);
      }
    }
  }

  private void addTokenIndex(String access, String refresh) {
    redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, access);
    redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refresh);
  }

  private void removeTokenIndex(String access, String refresh) {
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, access);
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refresh);
  }
}