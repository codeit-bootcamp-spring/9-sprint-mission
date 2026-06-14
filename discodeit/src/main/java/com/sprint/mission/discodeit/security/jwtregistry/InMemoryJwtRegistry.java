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

  private final Map<String, UUID> accessTokenIndex = new ConcurrentHashMap<>();
  private final Map<String, UUID> refreshTokenIndex = new ConcurrentHashMap<>();

  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    UUID userId = jwtInformation.getUserDto().id();

    origin.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>());
    Queue<JwtInformation> queue = origin.get(userId);

    synchronized (queue) {
      queue.add(jwtInformation);
      accessTokenIndex.put(jwtInformation.getAccessToken(), userId);
      refreshTokenIndex.put(jwtInformation.getRefreshToken(), userId);

      while (queue.size() > maxActiveJwtCount) {
        JwtInformation oldest = queue.poll();
        if (oldest != null) {
          accessTokenIndex.remove(oldest.getAccessToken());
          refreshTokenIndex.remove(oldest.getRefreshToken());
        }
        log.info("동시 로그인 제한 초과로 인해 기존 로그인 세션을 무효화 - userId: {}", userId);
      }
    }
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.remove(userId);
    if (queue != null) {
      synchronized (queue) {
        for (JwtInformation info : queue) {
          accessTokenIndex.remove(info.getAccessToken());
          refreshTokenIndex.remove(info.getRefreshToken());
        }
      }
    }
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    Queue<JwtInformation> queue = origin.get(userId);
    return queue != null && !queue.isEmpty();
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return accessTokenIndex.containsKey(accessToken);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return refreshTokenIndex.containsKey(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    UUID userId = refreshTokenIndex.get(refreshToken);
    if (userId == null) return;

    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      synchronized (queue) {
        for (JwtInformation info : queue) {
          if (info.getRefreshToken().equals(refreshToken)) {

            accessTokenIndex.remove(info.getAccessToken());
            refreshTokenIndex.remove(info.getRefreshToken());

            info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());

            accessTokenIndex.put(info.getAccessToken(), userId);
            refreshTokenIndex.put(info.getRefreshToken(), userId);
            break;
          }
        }
      }
    }
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
    UUID userId = refreshTokenIndex.get(refreshToken);
    if (userId != null) {
      removeTokenFromQueue(userId, null, refreshToken);
    }
  }

  @Override
  public void invalidateJwtInformationByAccessToken(String accessToken) {
    UUID userId = accessTokenIndex.get(accessToken);
    if (userId != null) {
      removeTokenFromQueue(userId, accessToken, null);
    }
  }

  private void removeTokenFromQueue(UUID userId, String accessToken, String refreshToken) {
    Queue<JwtInformation> queue = origin.get(userId);
    if (queue != null) {
      synchronized (queue) {
        queue.removeIf(info -> {
          boolean match = (accessToken != null && accessToken.equals(info.getAccessToken())) ||
              (refreshToken != null && refreshToken.equals(info.getRefreshToken()));
          if (match) {
            accessTokenIndex.remove(info.getAccessToken());
            refreshTokenIndex.remove(info.getRefreshToken());
          }
          return match;
        });
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    log.info("만료된 JWT 토큰 정리 스케줄러 실행");
    origin.entrySet().removeIf(entry -> {
      Queue<JwtInformation> queue = entry.getValue();
      synchronized (queue) {
        queue.removeIf(info -> {
          boolean isExpired = !jwtTokenProvider.validateToken(info.getAccessToken()) &&
              !jwtTokenProvider.validateToken(info.getRefreshToken());
          if (isExpired) {
            accessTokenIndex.remove(info.getAccessToken());
            refreshTokenIndex.remove(info.getRefreshToken());
          }
          return isExpired;
        });
      }
      return queue.isEmpty();
    });
  }

  @Override
  public List<UUID> getActiveUserIds() {
    return origin.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Map.Entry::getKey)
        .toList();
  }
}
