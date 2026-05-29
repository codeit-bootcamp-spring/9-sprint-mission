package com.sprint.mission.discodeit.security;


import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InMemoryJwtRegistry implements JwtRegistry {


  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserId();

    Queue<JwtInformation> queue = origin.computeIfAbsent(userId,
        k -> new ConcurrentLinkedQueue<>());

    while (queue.size() >= maxActiveJwtCount) {
      JwtInformation oldJwt = queue.poll();
      if (oldJwt != null) {
        log.info("[JwtRegistry] 동시 로그인 제한 발동 - 유저 [{}]의 기존 토큰 파기", userId);
      }
    }

    queue.offer(jwtInformation);
    log.info("[JwtRegistry] 유저 [{}] 신규 토큰 정보 장부 등록 완료", userId);
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {

    Queue<JwtInformation> queue = origin.remove(userId);
    if (queue != null) {
      queue.clear();
      log.info("[JwtRegistry] 강제 조치 - 유저 [{}]의 장부 데이터 완전 소거", userId);
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty() && queue.stream().anyMatch(jwt -> !jwt.isExpired());
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwt -> jwt.getAccessToken().equals(accessToken) && !jwt.isExpired());
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {

    return origin.values().stream()
        .flatMap(Queue::stream)
        .anyMatch(jwt -> jwt.getRefreshToken().equals(refreshToken) && !jwt.isExpired());
  }

  @Override
  public void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation) {

    UUID userId = newJwtInformation.getUserId();
    Queue<JwtInformation> queue = origin.get(userId);

    if (queue != null) {

      queue.removeIf(jwt -> jwt.getRefreshToken().equals(oldRefreshToken));
      queue.offer(newJwtInformation);
      log.info("[JwtRegistry] 유저 [{}] RTR 토큰 로테이션 장부 갱신 완료", userId);
    } else {

      registerJwtInformation(newJwtInformation);
    }
  }


  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    log.info("[JwtRegistry Scheduler] 만료 토큰 장부 청소 프로세스 가동");

    origin.forEach((userId, queue) -> {

      boolean removed = queue.removeIf(JwtInformation::isExpired);
      if (removed) {
        log.info("[JwtRegistry Scheduler] 유저 [{}]의 만료된 토큰 찌꺼기 청소 완료", userId);
      }

      if (queue.isEmpty()) {
        origin.remove(userId);
      }
    });
  }

  public void invalidateByRefreshToken(String refreshToken) {
    origin.forEach((userId, queue) -> {

      boolean removed = queue.removeIf(jwt -> jwt.getRefreshToken().equals(refreshToken));
      if (removed) {
        log.info("[JwtRegistry] 로그아웃 성공 - 유저 [{}]의 토큰 장부 적출 완수", userId);
      }
      if (queue.isEmpty()) {
        origin.remove(userId);
      }
    });
  }
}