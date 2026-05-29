package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

class JwtTokenProviderTest {

  private static final String SECRET = "test-jwt-secret-must-be-at-least-32-bytes";

  private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(
      SECRET,
      30 * 60 * 1000L,
      14 * 24 * 60 * 60 * 1000L
  );

  @Test
  void createAccessTokenCreatesValidToken() {
    Authentication authentication = authentication("testuser", "ROLE_USER");

    String accessToken = jwtTokenProvider.createAccessToken(authentication);

    assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
    assertThat(jwtTokenProvider.getUserIdFromToken(accessToken)).isEqualTo("testuser");
  }

  @Test
  void createRefreshTokenCreatesValidToken() {
    Authentication authentication = authentication("testuser", "ROLE_USER");

    String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

    assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
    assertThat(jwtTokenProvider.getUserIdFromToken(refreshToken)).isEqualTo("testuser");
  }

  @Test
  void getAuthenticationRestoresPrincipalAndAuthorities() {
    Authentication authentication = authentication("testuser", "ROLE_CHANNEL_MANAGER");
    String accessToken = jwtTokenProvider.createAccessToken(authentication);

    Authentication restored = jwtTokenProvider.getAuthentication(accessToken);

    assertThat(restored.getName()).isEqualTo("testuser");
    assertThat(restored.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactly("ROLE_CHANNEL_MANAGER");
  }

  @Test
  void validateTokenReturnsFalseWhenTokenIsExpired() {
    JwtTokenProvider expiredTokenProvider = new JwtTokenProvider(
        SECRET,
        -1L,
        14 * 24 * 60 * 60 * 1000L
    );
    String expiredToken = expiredTokenProvider.createAccessToken(authentication("testuser",
        "ROLE_USER"));

    assertThat(expiredTokenProvider.validateToken(expiredToken)).isFalse();
  }

  @Test
  void validateAccessTokenReturnsFalseForRefreshToken() {
    String refreshToken = jwtTokenProvider.createRefreshToken(authentication("testuser",
        "ROLE_USER"));

    assertThat(jwtTokenProvider.validateAccessToken(refreshToken)).isFalse();
    assertThat(jwtTokenProvider.validateRefreshToken(refreshToken)).isTrue();
  }

  private Authentication authentication(String username, String role) {
    Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
    return new UsernamePasswordAuthenticationToken(username, "password", authorities);
  }
}
