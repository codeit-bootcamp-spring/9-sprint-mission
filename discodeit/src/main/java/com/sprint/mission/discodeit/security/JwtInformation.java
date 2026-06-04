package com.sprint.mission.discodeit.security;

import java.time.Instant;
import java.util.UUID;

public record JwtInformation(
    UUID userId,
    String accessToken,
    String refreshToken,
    Instant expiresAt
) {

  public boolean isExpired(Instant now) {
    return !expiresAt.isAfter(now);
  }
}
