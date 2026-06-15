package com.sprint.mission.discodeit.secure;

import com.sprint.mission.discodeit.repository.JwtSessionRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryJwtRegistry implements JwtRegistry {

  private final JwtSessionRepository jwtSessionRepository;
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;

  @Override
  public void registerJwtInformation(UUID userId, JwtInformation info) {
    Queue<JwtInformation> queue = origin.computeIfAbsent(
        userId, k -> new ConcurrentLinkedQueue<>()
    );

    while (queue.size() >= maxActiveJwtCount) {
      queue.poll();
    }
    queue.add(info);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      queue.clear();
    }

    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null || queue.isEmpty()) {
      return false;
    }

    return queue.stream().anyMatch(info -> !info.isExpired());
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {

    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> !info.isExpired()
            && info.accessToken().equals(accessToken));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {

    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> !info.isExpired()
            && info.refreshToken().equals(refreshToken));
  }

  @Override
  public void rotateJwtInformation(UUID userId, JwtInformation oldInfo, JwtInformation newInfo) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      queue.remove(oldInfo);
      queue.add(newInfo);
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.values().forEach(queue -> queue.removeIf(JwtInformation::isExpired));
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    log.debug("만료된 JWT 정보 인메모리 정리 완료");

    deleteExpiredSessions();
  }

  @Transactional
  public void deleteExpiredSessions() {
    jwtSessionRepository.deleteByExpirationTimeBefore(Instant.now());
    log.debug("만료된 JWT 세션 DB 정리 완료");
  }

  @Override
  public Optional<JwtInformation> findByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Collection::stream)
        .filter(info -> info.refreshToken().equals(refreshToken))
        .findFirst();
  }
}
