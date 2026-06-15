package com.sprint.mission.discodeit.security;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.jwt.registry", havingValue = "in-memory")
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private static final int DEFAULT_MAX_ACTIVE_JWT_COUNT = 1;

  private final JwtTokenProvider jwtTokenProvider;
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = DEFAULT_MAX_ACTIVE_JWT_COUNT;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userDto().id();
    Queue<JwtInformation> jwtInformations =
        origin.computeIfAbsent(userId, ignored -> new ArrayDeque<>());

    synchronized (jwtInformations) {
      while (jwtInformations.size() >= maxActiveJwtCount) {
        jwtInformations.poll();
      }
      jwtInformations.add(jwtInformation);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.getOrDefault(userId, new ArrayDeque<>()).stream()
        .anyMatch(this::isActive);
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
    return origin.values().stream()
        .flatMap(Queue::stream)
        .filter(jwtInformation -> Objects.equals(jwtInformation.refreshToken(), refreshToken))
        .findFirst();
  }

  @Override
  public JwtInformation rotateJwtInformation(String refreshToken, String newAccessToken,
      String newRefreshToken) {
    JwtInformation current = findByRefreshToken(refreshToken)
        .orElseThrow(() -> new IllegalArgumentException("Refresh token is not registered"));
    JwtInformation rotated = current.rotate(newAccessToken, newRefreshToken,
        jwtTokenProvider.getExpiresAt(newRefreshToken));

    Queue<JwtInformation> jwtInformations = origin.get(current.userDto().id());
    if (jwtInformations == null) {
      throw new IllegalArgumentException("Refresh token is not registered");
    }

    synchronized (jwtInformations) {
      jwtInformations.remove(current);
      jwtInformations.add(rotated);
    }
    return rotated;
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    Instant now = Instant.now();
    origin.forEach((userId, jwtInformations) -> {
      synchronized (jwtInformations) {
        jwtInformations.removeIf(jwtInformation ->
            jwtInformation.refreshTokenExpiresAt().isBefore(now));
      }
      if (jwtInformations.isEmpty()) {
        origin.remove(userId, jwtInformations);
      }
    });
  }

  private Optional<JwtInformation> findByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .filter(jwtInformation -> Objects.equals(jwtInformation.accessToken(), accessToken))
        .findFirst();
  }

  private boolean isActive(JwtInformation jwtInformation) {
    return jwtInformation.refreshTokenExpiresAt().isAfter(Instant.now());
  }
}
