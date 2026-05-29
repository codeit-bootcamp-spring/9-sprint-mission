package com.sprint.mission.discodeit.security;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(
      @Value("${jwt.max-active-count:1}") int maxActiveJwtCount
  ) {
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.userDto().id();
    Queue<JwtInformation> queue = origin.computeIfAbsent(userId, ignored -> new ConcurrentLinkedQueue<>());

    synchronized (queue) {
      while (queue.size() >= maxActiveJwtCount) {
        queue.poll();
      }
      queue.add(jwtInformation);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.forEach((userId, queue) -> {
      queue.removeIf(jwtInformation -> jwtInformation.refreshToken().equals(refreshToken));
      if (queue.isEmpty()) {
        origin.remove(userId, queue);
      }
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && queue.stream().anyMatch(this::isActive);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwtInformation -> isActive(jwtInformation)
            && jwtInformation.accessToken().equals(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwtInformation -> isActive(jwtInformation)
            && jwtInformation.refreshToken().equals(refreshToken));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    Queue<JwtInformation> queue = origin.get(newJwtInformation.userDto().id());
    if (queue == null) {
      return;
    }

    synchronized (queue) {
      queue.removeIf(jwtInformation -> jwtInformation.refreshToken().equals(refreshToken));
      queue.add(newJwtInformation);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) -> {
      queue.removeIf(jwtInformation -> !isActive(jwtInformation));
      if (queue.isEmpty()) {
        origin.remove(userId, queue);
      }
    });
  }

  private boolean isActive(JwtInformation jwtInformation) {
    return jwtInformation.expiresAt().isAfter(Instant.now());
  }
}
