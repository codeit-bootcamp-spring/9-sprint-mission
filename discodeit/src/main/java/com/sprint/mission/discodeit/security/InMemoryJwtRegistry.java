package com.sprint.mission.discodeit.security;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
// @Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();
    origin.compute(userId, (id, queue) -> {
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      while (queue.size() >= maxActiveJwtCount) {
        queue.poll();
      }
      queue.add(jwtInformation);
      return queue;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.values().forEach(queue ->
        queue.removeIf(info -> info.getRefreshToken().equals(refreshToken))
    );
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getAccessToken().equals(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> info.getRefreshToken().equals(refreshToken));
  }

  @Override
  public Optional<JwtInformation> getJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Queue::stream)
        .filter(info -> info.getRefreshToken().equals(refreshToken))
        .findFirst();
  }

  @Override
  public java.util.Set<UUID> getAllOnlineUserIds() {
    return origin.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }

  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {
    UUID userId = newJwtInformation.getUserDto().id();
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      queue.removeIf(info -> info.getRefreshToken().equals(oldRefreshToken));
      queue.add(newJwtInformation);
    } else {
      registerJwtInformation(newJwtInformation);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    log.debug("Cleaning up expired JWT information...");
    origin.values().forEach(queue ->
        queue.removeIf(info -> !jwtTokenProvider.validateToken(info.getAccessToken()))
    );
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }
}
