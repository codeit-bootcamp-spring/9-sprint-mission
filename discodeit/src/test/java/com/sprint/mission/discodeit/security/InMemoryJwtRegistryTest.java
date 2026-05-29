package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  @Test
  void registerJwtInformationLimitsActiveJwtCount() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UserDto userDto = userDto();

    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access-1", "refresh-1",
        Instant.now().plusSeconds(60)));
    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access-2", "refresh-2",
        Instant.now().plusSeconds(60)));

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-1")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-2")).isTrue();
  }

  @Test
  void rotateJwtInformationReplacesRefreshTokenPair() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UserDto userDto = userDto();
    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access-1", "refresh-1",
        Instant.now().plusSeconds(60)));

    jwtRegistry.rotateJwtInformation("refresh-1", jwtInformation(userDto, "access-2",
        "refresh-2", Instant.now().plusSeconds(60)));

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-1")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-1")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-2")).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-2")).isTrue();
  }

  @Test
  void clearExpiredJwtInformationRemovesExpiredInformation() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UserDto userDto = userDto();
    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access", "refresh",
        Instant.now().minusSeconds(1)));

    jwtRegistry.clearExpiredJwtInformation();

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh")).isFalse();
  }

  @Test
  void invalidateJwtInformationByUserIdRemovesAllUserInformation() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(2);
    UserDto userDto = userDto();
    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access-1", "refresh-1",
        Instant.now().plusSeconds(60)));
    jwtRegistry.registerJwtInformation(jwtInformation(userDto, "access-2", "refresh-2",
        Instant.now().plusSeconds(60)));

    jwtRegistry.invalidateJwtInformationByUserId(userDto.id());

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userDto.id())).isFalse();
  }

  private JwtInformation jwtInformation(UserDto userDto, String accessToken, String refreshToken,
      Instant expiresAt) {
    return new JwtInformation(userDto, accessToken, refreshToken, expiresAt);
  }

  private UserDto userDto() {
    return new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, false, Role.USER);
  }
}
