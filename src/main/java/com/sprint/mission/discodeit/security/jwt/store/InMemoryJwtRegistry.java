package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@Component
@RequiredArgsConstructor
public class InMemoryJwtRegistry implements JwtRegistry {

  private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
  private final int maxActiveJwtCount = 1;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;

  @Override
  public void registerJwtInformation(JwtInformation jwtInformation) {
	UUID userId = jwtInformation.getUserDto().id();
	origin.compute(userId, (k, queue) -> {
	  if (queue == null) queue = new ConcurrentLinkedQueue<>();
	  queue.add(jwtInformation);
	  while (queue.size() > maxActiveJwtCount) {
		JwtInformation removed = queue.poll();
		if (removed != null) {
		  try {
			String refresh = removed.getRefreshToken();
			if (refresh != null) {
			  UUID jti = jwtTokenProvider.getJti(refresh);
			  if (jti != null) refreshTokenService.revoke(jti);
			}
		  } catch (Exception ignored) {
		  }
		}
	  }
	  return queue;
	});
  }

  @Override
  public void invalidateJwtInformationByUserId(UUID userId) {
	Queue<JwtInformation> q = origin.remove(userId);
	if (q != null) {
	  for (JwtInformation info : q) {
		try {
		  String refresh = info.getRefreshToken();
		  if (refresh != null) {
			UUID jti = jwtTokenProvider.getJti(refresh);
			if (jti != null) refreshTokenService.revoke(jti);
		  }
		} catch (Exception ignored) {
		}
	  }
	}
  }

  @Override
  public boolean hasActiveJwtInformationByUserId(UUID userId) {
	Queue<JwtInformation> q = origin.get(userId);
	if (q == null) return false;
	Instant now = Instant.now();
	return q.stream().anyMatch(info -> info.getRefreshExpiresAt().isAfter(now));
  }

  @Override
  public boolean hasActiveJwtInformationByAccessToken(String accessToken) {
	return origin.values().stream().flatMap(Queue::stream)
		.anyMatch(info -> accessToken.equals(info.getAccessToken()) && info.getAccessExpiresAt().isAfter(Instant.now()));
  }

  @Override
  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken) {
	return origin.values().stream().flatMap(Queue::stream)
		.anyMatch(info -> refreshToken.equals(info.getRefreshToken()) && info.getRefreshExpiresAt().isAfter(Instant.now()));
  }

  @Override
  public boolean rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
	for (Map.Entry<UUID, Queue<JwtInformation>> e : origin.entrySet()) {
	  Queue<JwtInformation> q = e.getValue();
	  for (JwtInformation info : q) {
		if (refreshToken.equals(info.getRefreshToken())) {
		  info.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken(), newJwtInformation.getAccessExpiresAt(), newJwtInformation.getRefreshExpiresAt());
		  return true;
		}
	  }
	}
	return false;
  }

  @Override
  public void clearExpiredJwtInformation() {
	Instant now = Instant.now();
	for (UUID userId : new ArrayList<>(origin.keySet())) {
	  Queue<JwtInformation> q = origin.get(userId);
	  if (q == null) continue;
	  q.removeIf(info -> info.getRefreshExpiresAt().isBefore(now));
	  if (q.isEmpty()) origin.remove(userId);
	}
  }

  @Override
  public void invalidateJwtInformationByRefreshToken(String refreshToken) {
	for (Map.Entry<UUID, Queue<JwtInformation>> e : origin.entrySet()) {
	  Queue<JwtInformation> q = e.getValue();
	  Iterator<JwtInformation> it = q.iterator();
	  while (it.hasNext()) {
		JwtInformation info = it.next();
		if (refreshToken.equals(info.getRefreshToken())) {
		  it.remove();
		  try {
			UUID jti = jwtTokenProvider.getJti(refreshToken);
			if (jti != null) refreshTokenService.revoke(jti);
		  } catch (Exception ignored) {
		  }
		}
	  }
	  if (q.isEmpty()) origin.remove(e.getKey());
	}
  }

  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void clearExpiredJwtInformationScheduled() {
	clearExpiredJwtInformation();
  }
}



