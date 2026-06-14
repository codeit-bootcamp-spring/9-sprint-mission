package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.dto.data.UserDto;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class JwtInformation {

  private final UserDto userDto;
  private String accessToken;
  private String refreshToken;
  private Instant accessExpiresAt;
  private Instant refreshExpiresAt;
  private final UUID id;

  public JwtInformation(UserDto userDto, String accessToken, String refreshToken, Instant accessExpiresAt, Instant refreshExpiresAt) {
    this.userDto = userDto;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.accessExpiresAt = accessExpiresAt;
    this.refreshExpiresAt = refreshExpiresAt;
    this.id = UUID.randomUUID();
  }

  public UserDto getUserDto() {
    return userDto;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public Instant getAccessExpiresAt() {
    return accessExpiresAt;
  }

  public Instant getRefreshExpiresAt() {
    return refreshExpiresAt;
  }

  public UUID getId() {
    return id;
  }

  public void rotate(String newAccessToken, String newRefreshToken, Instant newAccessExpiresAt, Instant newRefreshExpiresAt) {
    this.accessToken = newAccessToken;
    this.refreshToken = newRefreshToken;
    this.accessExpiresAt = newAccessExpiresAt;
    this.refreshExpiresAt = newRefreshExpiresAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JwtInformation that = (JwtInformation) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

