package com.sprint.mission.discodeit.secure.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record JwtInformation(
    UUID userId,
    String accessToken,
    String refreshToken,
    Instant expiresAt
) {

  @JsonCreator
  public JwtInformation(
      @JsonProperty("userId") UUID userId,
      @JsonProperty("accessToken") String accessToken,
      @JsonProperty("refreshToken") String refreshToken,
      @JsonProperty("expiresAt") Instant expiresAt
  ) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresAt = expiresAt;
  }

  public boolean isExpired() {
    return expiresAt.isBefore(Instant.now());
  }
}