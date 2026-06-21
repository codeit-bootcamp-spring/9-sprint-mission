package com.sprint.mission.discodeit.security;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(
    prefix = "discodeit.security.jwt-registry",
    name = "type",
    havingValue = "in-memory",
    matchIfMissing = true
)
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(
      @Value("${discodeit.security.token.max-active-count:1}") int maxActiveJwtCount
  ) {
    if (maxActiveJwtCount < 1) {
      throw new IllegalArgumentException("maxActiveJwtCount must be greater than 0");
    }
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.userId(), (userId, jwtInformations) -> {
      Queue<JwtInformation> activeJwtInformations = jwtInformations == null
          ? new ConcurrentLinkedQueue<>()
          : jwtInformations;
      removeExpired(activeJwtInformations, Instant.now());
      while (activeJwtInformations.size() >= maxActiveJwtCount) {
        activeJwtInformations.poll();
      }
      activeJwtInformations.offer(jwtInformation);
      return activeJwtInformations;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }
    origin.keySet().forEach(userId -> origin.computeIfPresent(userId, (id, jwtInformations) -> {
      jwtInformations.removeIf(jwtInformation -> refreshToken.equals(jwtInformation.refreshToken()));
      return jwtInformations.isEmpty() ? null : jwtInformations;
    }));
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> jwtInformations = origin.get(userId);
    return jwtInformations != null && hasActiveJwtInformation(jwtInformations);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    if (accessToken == null || accessToken.isBlank()) {
      return false;
    }
    return hasActiveJwtInformation(jwtInformation -> accessToken.equals(jwtInformation.accessToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return false;
    }
    return hasActiveJwtInformation(jwtInformation -> refreshToken.equals(jwtInformation.refreshToken()));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation rotatedJwtInformation) {
    if (!hasActiveJwtInformationByRefreshToken(refreshToken)) {
      return;
    }
    invalidateJwtInformationByRefreshToken(refreshToken);
    registerJwtInformation(rotatedJwtInformation);
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Instant now = Instant.now();
    origin.keySet().forEach(userId -> origin.computeIfPresent(userId, (id, jwtInformations) -> {
      removeExpired(jwtInformations, now);
      return jwtInformations.isEmpty() ? null : jwtInformations;
    }));
  }

  private boolean hasActiveJwtInformation(Queue<JwtInformation> jwtInformations) {
    removeExpired(jwtInformations, Instant.now());
    return !jwtInformations.isEmpty();
  }

  private boolean hasActiveJwtInformation(JwtInformationMatcher matcher) {
    Instant now = Instant.now();
    return origin.values().stream()
        .flatMap(Queue::stream)
        .filter(jwtInformation -> !jwtInformation.isExpired(now))
        .anyMatch(matcher::matches);
  }

  private void removeExpired(Queue<JwtInformation> jwtInformations, Instant now) {
    jwtInformations.removeIf(jwtInformation -> jwtInformation.isExpired(now));
  }

  @FunctionalInterface
  private interface JwtInformationMatcher {

    boolean matches(JwtInformation jwtInformation);
  }
}
