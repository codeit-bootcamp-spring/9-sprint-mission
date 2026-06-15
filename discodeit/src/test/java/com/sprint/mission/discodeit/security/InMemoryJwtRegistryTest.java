package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  private static final String SECRET = "test-jwt-secret-key-for-mission-10-provider";

  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
  private final InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(jwtTokenProvider);

  @Test
  void registerJwtInformation_LimitsSameUserToOneActiveJwtInformation() {
    UserDto userDto = userDto();
    JwtInformation first = jwtInformation(userDto);
    JwtInformation second = jwtInformation(userDto);

    jwtRegistry.registerJwtInformation(first);
    jwtRegistry.registerJwtInformation(second);

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(first.accessToken())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(second.accessToken())).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isTrue();
  }

  @Test
  void invalidateJwtInformationByUserId_RemovesAllUserJwtInformation() {
    UserDto userDto = userDto();
    JwtInformation jwtInformation = jwtInformation(userDto);
    jwtRegistry.registerJwtInformation(jwtInformation);

    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken(jwtInformation.accessToken()))
        .isFalse();
  }

  @Test
  void rotateJwtInformation_ReplacesAccessTokenAndRefreshToken() {
    UserDto userDto = userDto();
    JwtInformation jwtInformation = jwtInformation(userDto);
    String newAccessToken = jwtTokenProvider.generateAccessToken(userDto);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDto);
    jwtRegistry.registerJwtInformation(jwtInformation);

    JwtInformation rotated = jwtRegistry.rotateJwtInformation(
        jwtInformation.refreshToken(), newAccessToken, newRefreshToken);

    assertThat(rotated.accessToken()).isEqualTo(newAccessToken);
    assertThat(rotated.refreshToken()).isEqualTo(newRefreshToken);
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(jwtInformation.refreshToken()))
        .isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken(newRefreshToken)).isTrue();
  }

  @Test
  void clearExpiredJwtInformation_RemovesExpiredJwtInformation() {
    UserDto userDto = userDto();
    JwtInformation expiredJwtInformation = new JwtInformation(
        userDto,
        jwtTokenProvider.generateAccessToken(userDto),
        jwtTokenProvider.generateRefreshToken(userDto),
        Instant.now().minusSeconds(1)
    );
    jwtRegistry.registerJwtInformation(expiredJwtInformation);

    jwtRegistry.clearExpiredJwtInformation();

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
  }

  private JwtInformation jwtInformation(UserDto userDto) {
    String accessToken = jwtTokenProvider.generateAccessToken(userDto);
    String refreshToken = jwtTokenProvider.generateRefreshToken(userDto);
    return new JwtInformation(
        userDto,
        accessToken,
        refreshToken,
        jwtTokenProvider.getExpiresAt(refreshToken)
    );
  }

  private UserDto userDto() {
    return new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true, Role.USER);
  }
}
