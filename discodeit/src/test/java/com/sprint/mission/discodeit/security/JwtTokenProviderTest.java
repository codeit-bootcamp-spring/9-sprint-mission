package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  private static final String SECRET = "test-jwt-secret-key-for-mission-10-provider";

  @Test
  void generateAccessToken_ContainsUserClaims() {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.USER);

    String token = tokenProvider.generateAccessToken(userDto);

    assertThat(tokenProvider.validateToken(token)).isTrue();
    assertThat(tokenProvider.getUserId(token)).isEqualTo(userDto.id());
    assertThat(tokenProvider.getUsername(token)).isEqualTo(userDto.username());
    assertThat(tokenProvider.getRole(token)).isEqualTo(Role.USER);
  }

  @Test
  void refreshAccessToken_WithRefreshToken_GeneratesNewAccessToken() {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.CHANNEL_MANAGER);
    String refreshToken = tokenProvider.generateRefreshToken(userDto);

    String accessToken = tokenProvider.refreshAccessToken(refreshToken);

    assertThat(tokenProvider.validateToken(accessToken)).isTrue();
    assertThat(tokenProvider.getUserId(accessToken)).isEqualTo(userDto.id());
    assertThat(tokenProvider.getRole(accessToken)).isEqualTo(Role.CHANNEL_MANAGER);
  }

  @Test
  void refreshAccessToken_WithAccessToken_ThrowsException() {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.USER);
    String accessToken = tokenProvider.generateAccessToken(userDto);

    assertThatThrownBy(() -> tokenProvider.refreshAccessToken(accessToken))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void validateToken_WithExpiredToken_ReturnsFalse() {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, -1, 3600);
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.USER);

    String token = tokenProvider.generateAccessToken(userDto);

    assertThat(tokenProvider.validateToken(token)).isFalse();
  }

  @Test
  void constructor_WithShortSecret_ThrowsException() {
    assertThatThrownBy(() -> new JwtTokenProvider("short", 1800, 3600))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
