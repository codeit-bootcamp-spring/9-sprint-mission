package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.Instant;

public record JwtInformation(
    UserDto userDto,
    String accessToken,
    String refreshToken,
    Instant expiresAt
) {

  public JwtInformation rotate(String accessToken, String refreshToken, Instant expiresAt) {
    return new JwtInformation(userDto, accessToken, refreshToken, expiresAt);
  }
}
