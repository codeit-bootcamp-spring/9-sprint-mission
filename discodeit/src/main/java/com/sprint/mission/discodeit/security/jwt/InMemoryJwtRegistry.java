package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.dto.data.JwtInformation;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;


@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  // <userId, Queue<JwtInformation>>
  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final Set<String> accessTokenIndexes = ConcurrentHashMap.newKeySet();
  private final Set<String> refreshTokenIndexes = ConcurrentHashMap.newKeySet();

  private final int maxActiveJwtCount;
  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
    origin.compute(jwtInformation.getUserDto().id(), (key, queue) -> {
      if (queue == null) {
        queue = new ConcurrentLinkedQueue<>();
      }
      // If the queue exceeds the max size, remove the oldest token
      if (queue.size() >= maxActiveJwtCount) {
        JwtInformation deprecatedJwtInformation = queue.poll();// Remove the oldest token
        if (deprecatedJwtInformation != null) {
          removeTokenIndex(
              deprecatedJwtInformation.getAccessToken(),
              deprecatedJwtInformation.getRefreshToken()
          );
        }
      }
      queue.add(jwtInformation); // Add the new token
      addTokenIndex(
          jwtInformation.getAccessToken(),
          jwtInformation.getRefreshToken()
      );
      return queue;
    });
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
    origin.computeIfPresent(userId, (key, queue) -> {
      queue.forEach(jwtInformation -> {
        removeTokenIndex(
            jwtInformation.getAccessToken(),
            jwtInformation.getRefreshToken()
        );
      });
      queue.clear(); // Clear the queue for this user
      return null; // Remove the user from the registry
    });
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
    return origin.containsKey(userId);
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
    return accessTokenIndexes.contains(accessToken);
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
    return refreshTokenIndexes.contains(refreshToken);
  }

  @Override
  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
    origin.computeIfPresent(newJwtInformation.getUserDto().id(), (key, queue) -> {
      queue.stream().filter(jwtInformation -> jwtInformation.getRefreshToken().equals(refreshToken))
          .findFirst()
          .ifPresent(jwtInformation -> {
            removeTokenIndex(jwtInformation.getAccessToken(), jwtInformation.getRefreshToken());
            jwtInformation.rotate(
                newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken()
            );
            addTokenIndex(
                newJwtInformation.getAccessToken(),
                newJwtInformation.getRefreshToken()
            );
          });
      return queue;
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  @Override
  public void clearExpiredJwtInformation() {
    origin.entrySet().removeIf(entry -> {
      Queue<JwtInformation> queue = entry.getValue();
      queue.removeIf(jwtInformation -> {
        boolean isExpired =
            !jwtTokenProvider.validateAccessToken(jwtInformation.getAccessToken()) ||
                !jwtTokenProvider.validateRefreshToken(jwtInformation.getRefreshToken());
        if (isExpired) {
          removeTokenIndex(
              jwtInformation.getAccessToken(),
              jwtInformation.getRefreshToken()
          );
        }
        return isExpired;
      });
      return queue.isEmpty(); // Remove the entry if the queue is empty
    });
  }

  private void addTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.add(accessToken);
    refreshTokenIndexes.add(refreshToken);
  }

  private void removeTokenIndex(String accessToken, String refreshToken) {
    accessTokenIndexes.remove(accessToken);
    refreshTokenIndexes.remove(refreshToken);
  }
}
