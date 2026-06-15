package com.sprint.mission.discodeit.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshTokenStore {
    private final Map<String,RefreshTokenInfo> store = new ConcurrentHashMap<>();
    private final long refreshTokenValiditySeconds;

    public RefreshTokenStore(
            @Value("${jwt.refresh-token-validity-seconds}")
            long refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void save(String refreshToken, String username) {
        store.put(refreshToken, new RefreshTokenInfo(username, Instant.now().plusSeconds(refreshTokenValiditySeconds)));
    }

    public Optional<String> findUsername(String refreshToken) {
        RefreshTokenInfo info = store.get(refreshToken);

        if (info == null) {
            return Optional.empty();
        }
        if (info.expiresAt().isBefore(Instant.now())) {
            store.remove(refreshToken);
            return Optional.empty();
        }
        return Optional.of(info.username());
    }

    public void remove(String refreshToken) {
        store.remove(refreshToken);
    }

    public record RefreshTokenInfo(String username, Instant expiresAt) {}


}
