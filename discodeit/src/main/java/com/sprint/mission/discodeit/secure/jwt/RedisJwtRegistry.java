package com.sprint.mission.discodeit.secure.jwt;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisJwtRegistry implements JwtRegistry {

  private static final String USER_JWT_KEY_PREFIX = "jwt:user:";
  private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
  private static final int MAX_ACTIVE_JWT_COUNT = 1;

  private final RedisTemplate<String, Object> redisTemplate;
  private final JwtTokenProvider jwtTokenProvider;

  private String getUserKey(UUID userId) {
    return USER_JWT_KEY_PREFIX + userId.toString();
  }

  @Override
  public void registerJwtInformation(UUID userId, JwtInformation info) {
    String key = getUserKey(userId);

    Long currentSize = redisTemplate.opsForList().size(key);
    while (currentSize != null && currentSize >= MAX_ACTIVE_JWT_COUNT) {
      redisTemplate.opsForList().leftPop(key);
      currentSize = redisTemplate.opsForList().size(key);
    }

    redisTemplate.opsForList().rightPush(key, info);
    redisTemplate.expire(key, DEFAULT_TTL);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    redisTemplate.delete(getUserKey(userId));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    String key = getUserKey(userId);
    Long size = redisTemplate.opsForList().size(key);
    if (size == null || size == 0) {
      return false;
    }

    List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
    return tokens != null && tokens.stream()
        .filter(obj -> obj instanceof JwtInformation)
        .map(obj -> (JwtInformation) obj)
        .anyMatch(info -> !info.isExpired());
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    var keys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (keys == null) {
      return false;
    }

    return keys.stream().anyMatch(key -> {
      List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
      return tokens != null && tokens.stream()
          .filter(obj -> obj instanceof JwtInformation)
          .map(obj -> (JwtInformation) obj)
          .anyMatch(info -> !info.isExpired() && info.accessToken().equals(accessToken));
    });
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    var keys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (keys == null) {
      return false;
    }

    return keys.stream().anyMatch(key -> {
      List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
      return tokens != null && tokens.stream()
          .filter(obj -> obj instanceof JwtInformation)
          .map(obj -> (JwtInformation) obj)
          .anyMatch(info -> !info.isExpired() && info.refreshToken().equals(refreshToken));
    });
  }

  @Override
  public void rotateJwtInformation(UUID userId, JwtInformation oldInfo, JwtInformation newInfo) {
    String key = getUserKey(userId);
    List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
    if (tokens == null) {
      return;
    }

    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i) instanceof JwtInformation info &&
          info.refreshToken().equals(oldInfo.refreshToken())) {
        redisTemplate.opsForList().set(key, i, newInfo);
        redisTemplate.expire(key, DEFAULT_TTL);
        return;
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    var keys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (keys == null) {
      return;
    }

    for (String key : keys) {
      List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
      if (tokens == null) {
        continue;
      }

      for (int i = 0; i < tokens.size(); i++) {
        if (tokens.get(i) instanceof JwtInformation info && info.isExpired()) {
          redisTemplate.opsForList().set(key, i, "EXPIRED");
          redisTemplate.opsForList().remove(key, 1, "EXPIRED");
        }
      }
      
      Long remaining = redisTemplate.opsForList().size(key);
      if (remaining == null || remaining == 0) {
        redisTemplate.delete(key);
      }
    }
    log.debug("만료된 JWT 정보 Redis 정리 완료");
  }

  @Override
  public Optional<JwtInformation> findByRefreshToken(String refreshToken) {
    var keys = redisTemplate.keys(USER_JWT_KEY_PREFIX + "*");
    if (keys == null) {
      return Optional.empty();
    }

    return keys.stream()
        .flatMap(key -> {
          List<Object> tokens = redisTemplate.opsForList().range(key, 0, -1);
          return tokens == null ? java.util.stream.Stream.empty() : tokens.stream();
        })
        .filter(obj -> obj instanceof JwtInformation)
        .map(obj -> (JwtInformation) obj)
        .filter(info -> info.refreshToken().equals(refreshToken))
        .findFirst();
  }
}

