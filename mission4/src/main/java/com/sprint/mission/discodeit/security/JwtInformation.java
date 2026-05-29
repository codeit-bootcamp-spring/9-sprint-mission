package com.sprint.mission.discodeit.security;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtInformation {

  private final UUID userId;
  private final String accessToken;
  private final String refreshToken;
  private final LocalDateTime accessTokenExpiresAt;
  private final LocalDateTime refreshTokenExpiresAt;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(this.refreshTokenExpiresAt);
  }

}
