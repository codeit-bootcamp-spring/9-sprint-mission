package com.sprint.mission.discodeit.security.init;

import com.sprint.mission.discodeit.entity.event.UserLogInOutEvent;
import com.sprint.mission.discodeit.redis.RedisLockProvider.RedisLockAcquisitionException;
import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
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
import org.springframework.stereotype.Component;

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

  @CacheEvict(value = "users", key = "'all'")
  @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    String userKey = getUserKey(jwtInformation.getUserDto().id());
    String lockKey = jwtInformation.getUserDto().id().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      Long currentSize = redisTemplate.opsForList().size(userKey);

      while (currentSize != null && currentSize >= maxActiveJwtCount) {
        Object oldestTokenObj = redisTemplate.opsForList().leftPop(userKey);
        if (oldestTokenObj instanceof JwtInformation oldestToken) {
          removeTokenIndex(oldestToken.getAccessToken(), oldestToken.getRefreshToken());
        }
        currentSize = redisTemplate.opsForList().size(userKey);
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

  @CacheEvict(value = "users", key = "'all'")
  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);

    List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);
    if (tokens != null) {
      tokens.forEach(tokenObj -> {
        if (tokenObj instanceof JwtInformation jwtInfo) {
          removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
        }
      });
    }

    redisTemplate.delete(userKey);
    eventPublisher.publishEvent(new UserLogInOutEvent(userId, false));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    String userKey = getUserKey(userId);
    Long size = redisTemplate.opsForList().size(userKey);
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

  @Retryable(retryFor = RedisLockAcquisitionException.class, maxAttempts = 10,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
    String userKey = getUserKey(newJwtInformation.getUserDto().id());
    String lockKey = newJwtInformation.getUserDto().id().toString();

    redisLockProvider.acquireLock(lockKey);
    try {
      List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

      if (tokens != null) {
        for (int i = 0; i < tokens.size(); i++) {
          if (tokens.get(i) instanceof JwtInformation jwtInfo &&
              jwtInfo.getRefreshToken().equals(oldRefreshToken)) { // 파라미터명 변경

            removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
            jwtInfo.rotate(newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken());
            redisTemplate.opsForList().set(userKey, i, jwtInfo);
            addTokenIndex(newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken());
            redisTemplate.expire(userKey, DEFAULT_TTL);
            break;
          }
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

    if (userKeys != null) {
      for (String userKey : userKeys) {
        List<Object> tokens = redisTemplate.opsForList().range(userKey, 0, -1);

        if (tokens != null) {
          boolean hasValidTokens = false;

          for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i) instanceof JwtInformation jwtInfo) {
              boolean isExpired =
                  !jwtTokenProvider.validateToken(jwtInfo.getAccessToken()) ||
                      !jwtTokenProvider.validateToken(jwtInfo.getRefreshToken());

              if (isExpired) {
                redisTemplate.opsForList().set(userKey, i, "EXPIRED");
                redisTemplate.opsForList().remove(userKey, 1, "EXPIRED");
                removeTokenIndex(jwtInfo.getAccessToken(), jwtInfo.getRefreshToken());
              } else {
                hasValidTokens = true;
              }
            }
          }

          if (!hasValidTokens) {
            redisTemplate.delete(userKey);
          }
        }
      }
    }
  }

  private String getUserKey(UUID userId) {
    return USER_JWT_KEY_PREFIX + userId.toString();
  }

  private void addTokenIndex(String accessToken, String refreshToken) {
    redisTemplate.opsForSet().add(ACCESS_TOKEN_INDEX_KEY, accessToken);
    redisTemplate.opsForSet().add(REFRESH_TOKEN_INDEX_KEY, refreshToken);
    redisTemplate.expire(ACCESS_TOKEN_INDEX_KEY, DEFAULT_TTL);
    redisTemplate.expire(REFRESH_TOKEN_INDEX_KEY, DEFAULT_TTL);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    redisTemplate.opsForSet().remove(ACCESS_TOKEN_INDEX_KEY, accessToken);
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    redisTemplate.opsForSet().remove(REFRESH_TOKEN_INDEX_KEY, refreshToken);
  }
}