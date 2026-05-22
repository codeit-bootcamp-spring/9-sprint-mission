package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryJwtRegistryTest {

  @Test
  @DisplayName("registerJwtInformation 성공: 최대 동시 로그인 수를 초과하면 기존 토큰을 무효화한다")
  void registerJwtInformation_exceedsMaxActiveCount_invalidatesOldToken() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UUID userId = UUID.randomUUID();
    JwtInformation oldJwtInformation = new JwtInformation(
        userId,
        "old-access-token",
        "old-refresh-token",
        Instant.now().plusSeconds(60)
    );
    JwtInformation newJwtInformation = new JwtInformation(
        userId,
        "new-access-token",
        "new-refresh-token",
        Instant.now().plusSeconds(60)
    );

    jwtRegistry.registerJwtInformation(oldJwtInformation);
    jwtRegistry.registerJwtInformation(newJwtInformation);

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("old-access-token")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("old-refresh-token")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("new-access-token")).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("new-refresh-token")).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isTrue();
  }

  @Test
  @DisplayName("rotateJwtInformation 성공: refresh token 로테이션 시 이전 토큰을 무효화한다")
  void rotateJwtInformation_success_invalidatesOldToken() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UUID userId = UUID.randomUUID();
    JwtInformation beforeRotation = new JwtInformation(
        userId,
        "access-token",
        "refresh-token",
        Instant.now().plusSeconds(60)
    );
    JwtInformation afterRotation = new JwtInformation(
        userId,
        "rotated-access-token",
        "rotated-refresh-token",
        Instant.now().plusSeconds(60)
    );

    jwtRegistry.registerJwtInformation(beforeRotation);
    jwtRegistry.rotateJwtInformation("refresh-token", afterRotation);

    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-token")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-token")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("rotated-access-token")).isTrue();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("rotated-refresh-token")).isTrue();
  }

  @Test
  @DisplayName("clearExpiredJwtInformation 성공: 만료된 토큰 정보를 삭제한다")
  void clearExpiredJwtInformation_success_removesExpiredToken() {
    InMemoryJwtRegistry jwtRegistry = new InMemoryJwtRegistry(1);
    UUID userId = UUID.randomUUID();
    JwtInformation expiredJwtInformation = new JwtInformation(
        userId,
        "access-token",
        "refresh-token",
        Instant.now().minusSeconds(1)
    );

    jwtRegistry.registerJwtInformation(expiredJwtInformation);
    jwtRegistry.clearExpiredJwtInformation();

    assertThat(jwtRegistry.hasActiveJwtInformationByUserId(userId)).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByAccessToken("access-token")).isFalse();
    assertThat(jwtRegistry.hasActiveJwtInformationByRefreshToken("refresh-token")).isFalse();
  }
}
