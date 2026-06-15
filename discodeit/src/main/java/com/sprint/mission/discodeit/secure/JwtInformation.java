package com.sprint.mission.discodeit.secure;

import java.time.Instant;
import java.util.UUID;

public record JwtInformation(
    UUID userId,
    String accessToken,
    String refreshToken,
    Instant expiresAt
) {

  public boolean isExpired() {
    return expiresAt.isBefore(Instant.now());
  }
}