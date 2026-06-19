package com.sprint.mission.discodeit.dto.data;

import lombok.Getter;

@Getter
public class JwtInformation {

  private final UserDto userDto;
  private String accessToken;
  private String refreshToken;

  public JwtInformation(UserDto userDto, String accessToken, String refreshToken) {
    this.userDto = userDto;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public void rotate(String newAccessToken, String newRefreshToken) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
  }
}