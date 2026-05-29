package com.sprint.mission.discodeit.security.registry;

import com.sprint.mission.discodeit.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {

  private final JwtTokenProvider jwtTokenProvider;
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;

  public InMemoryJwtRegistry(
      JwtTokenProvider jwtTokenProvider,
      @Value("${discodeit.security.max-active-jwt-count:1}") int maxActiveJwtCount
  ) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.maxActiveJwtCount = maxActiveJwtCount;
  }

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    if (jwtInformation == null || jwtInformation.getUserDto() == null) {
      return;
    }

    UUID userId = jwtInformation.getUserDto().id();
    Queue<JwtInformation> queue = origin.computeIfAbsent(userId,
        k -> new ConcurrentLinkedQueue<>());

    while (queue.size() >= maxActiveJwtCount) {
      JwtInformation oldest = queue.poll();
      if (oldest != null) {
        log.info("동시 로그인 제한으로 기존 세션 무효화 처리 - UserId: {}, AccessToken: {}", userId,
            oldest.getAccessToken());
      }
    }

    queue.offer(jwtInformation);
    log.debug("새로운 JWT 등록 완료 - UserId: {}, 현재 활성 세션 수: {}", userId, queue.size());
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    if (userId == null) {
      return;
    }
    if (origin.remove(userId) != null) {
      log.info("유저의 모든 JwtInformation 삭제 완료 - UserId: {}", userId);
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    if (userId == null) {
      return false;
    }
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null || queue.isEmpty()) {
      return false;
    }

    return queue.stream().anyMatch(info -> jwtTokenProvider.validateToken(info.getRefreshToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    if (accessToken == null) {
      return false;
    }

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> accessToken.equals(info.getAccessToken())
            && jwtTokenProvider.validateToken(info.getAccessToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    if (refreshToken == null) {
      return false;
    }

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(info -> refreshToken.equals(info.getRefreshToken())
            && jwtTokenProvider.validateToken(info.getRefreshToken()));
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    if (refreshToken == null || newJwtInformation == null) {
      return;
    }

    origin.values().stream()
        .flatMap(Queue::stream)
        .filter(info -> refreshToken.equals(info.getRefreshToken()))
        .findFirst()
        .ifPresentOrElse(
            info -> {
              info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
              log.debug("JWT 토큰 로테이션 완료 - UserId: {}", info.getUserDto().id());
            },
            () -> registerJwtInformation(newJwtInformation)
        );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    log.debug("만료된 인메모리 JWT 정보 청소 스케줄러 가동 (5분 주기)");

    origin.forEach((userId, queue) -> {
      queue.removeIf(info -> !jwtTokenProvider.validateToken(info.getRefreshToken()));
      if (queue.isEmpty()) {
        origin.remove(userId);
      }
    });
  }

  public void invalidateByRefreshToken(String refreshToken) {
    if (refreshToken == null) {
      return;
    }
    origin.forEach((userId, queue) -> {
      boolean removed = queue.removeIf(info -> refreshToken.equals(info.getRefreshToken()));
      if (removed) {
        log.info("리프레시 토큰 무효화로 세션 제거 완료 - UserId: {}", userId);
      }
    });
  }
}