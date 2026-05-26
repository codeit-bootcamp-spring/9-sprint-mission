package com.sprint.mission.discodeit.security;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;

  public InMemoryJwtRegistry(
      JwtTokenProvider jwtTokenProvider,
      @Value("${jwt.max-active-count:1}") int maxActiveJwtCount
  ) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();
    Queue<JwtInformation> queue = origin.computeIfAbsent(userId, k -> new LinkedBlockingQueue<>());

    while (queue.size() >= maxActiveJwtCount) {
      queue.poll();
    }
    queue.add(jwtInformation);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> accessToken.equals(info.getAccessToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info -> refreshToken.equals(info.getRefreshToken()));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    origin.values().stream()
        .flatMap(Collection::stream)
        .filter(info -> refreshToken.equals(info.getRefreshToken()))
        .findFirst()
        .ifPresent(info -> info.rotate(
            newJwtInformation.getAccessToken(),
            newJwtInformation.getRefreshToken()
        ));
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) ->
        queue.removeIf(info -> {
          try {
            jwtTokenProvider.validateAndGetClaims(info.getRefreshToken());
            return false;
          } catch (Exception e) {
            return true;
          }
        })
    );
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    log.debug("만료된 JWT 정보 정리 완료");
  }
}
