package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.JwtInformation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class InMemoryJwtRegistry implements JwtRegistry{

    private final Map<UUID, Queue<JwtInformation>> origin = new ConcurrentHashMap<>();
    public final int maxActiveJwtCount = 1;

    @Override
    public JwtInformation registerJwtInformation(JwtInformation jwtInformation) {
        UUID userId = jwtInformation.getUserDto().id();

        Queue<JwtInformation> queue = origin.computeIfAbsent(userId, k -> new ConcurrentLinkedDeque<>());
        while (queue.size() > maxActiveJwtCount) {
            queue.poll();
        }
        queue.offer(jwtInformation);
        return jwtInformation;
    }

    @Override
    public boolean invalidateJwtInformationByUserId(UUID userId) {
        return origin.remove(userId) != null;
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
    public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation) {
        origin.values().stream()
                .flatMap(Queue::stream)
                .filter(info -> info.getRefreshToken().equals(refreshToken))
                .findFirst()
                .ifPresent(oldInfo -> {
                    oldInfo.rotate(newJwtInformation.getAccessToken(), newJwtInformation.getRefreshToken());
                });
    }

    @Scheduled(fixedDelay = 1000 * 60 * 5)
    @Override
    public void clearExpiredJwtInformation() {
        origin.forEach((userId, queue) -> {
            queue.removeIf(JwtInformation::isExpired);
            if (queue.isEmpty()) {
                origin.remove(userId);
            }
        });
    }
    public void invalidateJwtInformationByRefreshToken(String refreshToken) {
        origin.values().forEach(queue -> {
            queue.removeIf(info -> info.getRefreshToken().equals(refreshToken));
        });
    }
}
