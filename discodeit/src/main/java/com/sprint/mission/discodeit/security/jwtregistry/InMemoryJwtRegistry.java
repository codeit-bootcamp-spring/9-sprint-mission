package com.sprint.mission.discodeit.security.jwtregistry;

import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();

    origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
    Queue<JwtInformation> queue = origin.get(userId);

    queue.add(jwtInformation);

    while (queue.size() > maxActiveJwtCount) {
      queue.poll();
      log.info("동시 로그인 제한 초과로 인해 기존 로그인 세션을 무효화 - userId: {}", userId);
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId) && !origin.get(userId).isEmpty();
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
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    origin.values().stream()
        .flatMap(Queue::stream)
        .filter(info -> info.getRefreshToken().equals(refreshToken))
        .findFirst()
        .ifPresent(info -> info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken()));
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    origin.values().forEach(queue ->
        queue.removeIf(info -> info.getRefreshToken().equals(refreshToken))
    );
  }

  @Override
  public void invalidateJwtInformationByAccessToken(String accessToken) {
    origin.values().forEach(queue ->
        queue.removeIf(info -> info.getAccessToken().equals(accessToken))
    );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    log.info("만료된 JWT 토큰 정리 스케줄러 실행");
    origin.values().forEach(queue ->
        queue.removeIf(info ->
            !jwtTokenProvider.validateToken(info.getAccessToken()) &&
                !jwtTokenProvider.validateToken(info.getRefreshToken())
        )
    );
  }

  @Override
  public List<UUID> getActiveUserIds() {
    return origin.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .toList();
  }
}
