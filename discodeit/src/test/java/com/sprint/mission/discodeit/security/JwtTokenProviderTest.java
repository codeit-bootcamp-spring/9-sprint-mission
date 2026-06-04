package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class JwtTokenProviderTest {

  @Test
  @DisplayName("createToken 성공: JWT를 발급하고 subject를 조회한다")
  void createToken_success() {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
        "test-token-secret-key-for-hs256-32bytes",
        3600,
        604800
    );
    UserResponse userResponse = new UserResponse(
        UUID.randomUUID(), "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");

    String token = jwtTokenProvider.createToken(userDetails);

    assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    assertThat(jwtTokenProvider.getUsername(token)).isEqualTo("jun");
  }

  @Test
  @DisplayName("validateToken 실패: 만료된 JWT는 유효하지 않다")
  void validateToken_fail_expired() {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
        "test-token-secret-key-for-hs256-32bytes",
        -1,
        604800
    );
    UserResponse userResponse = new UserResponse(
        UUID.randomUUID(), "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");

    String token = jwtTokenProvider.createToken(userDetails);

    assertThat(jwtTokenProvider.validateToken(token)).isFalse();
    assertThrows(BadCredentialsException.class, () -> jwtTokenProvider.getUsername(token));
  }

  @Test
  @DisplayName("validateToken 실패: 변조된 JWT는 예외가 발생한다")
  void validateToken_fail_tampered() {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
        "test-token-secret-key-for-hs256-32bytes",
        3600,
        604800
    );

    assertThrows(BadCredentialsException.class, () -> jwtTokenProvider.validateToken("invalid.jwt.token"));
  }

  @Test
  @DisplayName("refreshToken 성공: refresh token은 access token과 별도 만료 시간을 사용한다")
  void refreshToken_success_usesRefreshExpiration() {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
        "test-token-secret-key-for-hs256-32bytes",
        -1,
        60
    );
    UserResponse userResponse = new UserResponse(
        UUID.randomUUID(), "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");

    String accessToken = jwtTokenProvider.createToken(userDetails);
    String refreshToken = jwtTokenProvider.refreshToken(userDetails);

    assertThat(jwtTokenProvider.validateToken(accessToken)).isFalse();
    assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
    assertThat(jwtTokenProvider.getUsername(refreshToken)).isEqualTo("jun");
  }
}
