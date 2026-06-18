package com.sprint.mission.discodeit.security;


import com.sprint.mission.discodeit.dto.data.UserDto;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class JwtInformation {

  private UserDto userDto;
  private String accessToken;
  private String refreshToken;
  private LocalDateTime accessTokenExpiresAt;
  private LocalDateTime refreshTokenExpiresAt;

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(this.refreshTokenExpiresAt);
  }


  public void rotate(String newAccessToken, String newRefreshToken) {
    if (newAccessToken == null || newAccessToken.isEmpty()) {
      throw new IllegalArgumentException("accessToken cannot be empty");
    }
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }
}
