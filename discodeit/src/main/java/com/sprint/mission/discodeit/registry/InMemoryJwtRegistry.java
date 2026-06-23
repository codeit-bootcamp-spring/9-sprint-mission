package com.sprint.mission.discodeit.registry;

import com.sprint.mission.discodeit.config.JwtTokenProvider;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.beans.factory.annotation.Value;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// @Component
// redis 방식으로 바꿀거라서 일단 등록 x
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;

  public InMemoryJwtRegistry(
      @Value("${jwt.max-active-count:1}") int maxActiveJwtCount,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.maxActiveJwtCount = maxActiveJwtCount;
    this.jwtTokenProvider = jwtTokenProvider;
  }


  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    /* 로그인 성공 시 JwtInformation을 등록합니다.
      최대 동시 로그인 수(1)를 제어합니다 */
    UUID userId = jwtInformation.getUserDto().id();
    origin.compute(userId, (id, queue) -> {
      // 로그인 세션 큐 없으면 생성
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      // 큐 사이즈가 동시 로그인 수 초과하면 오래된 거 제거
      while (queue.size() >= maxActiveJwtCount) {
        queue.poll();
      }
      // 큐에 정보 넣어주고
      queue.offer(jwtInformation);
      return queue;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    /* UserId로 해당 유저의 모든 JwtInformation 정보를 삭제합니다. */
    origin.remove(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    /* JwtInformation이 Registry에 존재하는지 확인합니다. */
    /* 사용자의 로그인 상태를 판단할 때 활용 */
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue == null || queue.isEmpty()) {
      return false;
    }
    return queue.stream()
        .anyMatch(info -> !isTokenExpired(info.getRefreshToken()));
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    /* JwtInformation이 Registry에 존재하는지 확인합니다. */
    /* 필터에서 유효한 토큰인지 확인할 때 활용 */
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info ->
            info.getAccessToken().equals(accessToken)
                && !isTokenExpired(info.getRefreshToken())
        );
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    /* JwtInformation이 Registry에 존재하는지 확인합니다. */
    /* 토큰 재발급 시 유효한 토큰인지 확인할 때 활용 */
    return origin.values().stream()
        .flatMap(Collection::stream)
        .anyMatch(info ->
            info.getRefreshToken().equals(refreshToken)
                && !isTokenExpired(info.getRefreshToken())
        );
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    /* 토큰 재발급 시 토큰 로테이션을 수행합니다. */
    UUID userId = newJwtInformation.getUserDto().id();

    origin.computeIfPresent(userId, (id, queue) -> {
      queue.stream()
          .filter(info -> info.getRefreshToken().equals(refreshToken))
          .findFirst()
          // JwtInformation.rotate()로 내부 상태 갱신
          .ifPresent(info -> info.rotate(
              newJwtInformation.getAccessToken(),
              newJwtInformation.getRefreshToken()
          ));
      return queue;
    });
  }

  @Override
  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void clearExpiredJwtInformation() {
    origin.forEach((userId, queue) ->
        queue.removeIf(info -> isTokenExpired(info.getRefreshToken()))
    );
    // 빈 큐 정리
    origin.entrySet().removeIf(entry -> entry.getValue().isEmpty());
  }

  private boolean isTokenExpired(String token) {
    try {
      return !jwtTokenProvider.validateToken(token);
    } catch (Exception e) {
      return true;
    }
  }

  @Override
  public Optional<UUID> findUserIdByRefreshToken(String refreshToken) {
    return origin.entrySet().stream()
        .filter(entry -> entry.getValue().stream()
            .anyMatch(info -> info.getRefreshToken().equals(refreshToken)))
        .map(Map.Entry::getKey)
        .findFirst();
  }
}
