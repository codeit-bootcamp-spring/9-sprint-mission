package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenIssuer {

  public static final String TOKEN_TYPE = "Bearer";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  public JwtIssue issue(DiscodeitUserDetails userDetails) {
    String accessToken = jwtTokenProvider.createToken(userDetails);
    String refreshToken = jwtTokenProvider.refreshToken(userDetails);
    Instant accessExpiresAt = Instant.now().plusSeconds(jwtTokenProvider.getExpirationSeconds());
    Instant refreshExpiresAt = Instant.now().plusSeconds(jwtTokenProvider.getRefreshExpirationSeconds());
    JwtInformation jwtInformation = new JwtInformation(
        userDetails.getUserDto().id(),
        accessToken,
        refreshToken,
        refreshExpiresAt
    );

    jwtRegistry.registerJwtInformation(jwtInformation);

    return createIssue(userDetails, accessToken, refreshToken, accessExpiresAt);
  }

  public JwtIssue refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new BadCredentialsException("Refresh token is missing");
    }
    if (!jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
      throw new BadCredentialsException("Inactive refresh token");
    }

    String username = jwtTokenProvider.getUsername(refreshToken);
    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) userDetailsService.loadUserByUsername(username);
    return rotate(refreshToken, userDetails);
  }

  private JwtIssue rotate(String refreshToken, DiscodeitUserDetails userDetails) {
    String accessToken = jwtTokenProvider.createToken(userDetails);
    String rotatedRefreshToken = jwtTokenProvider.refreshToken(userDetails);
    Instant accessExpiresAt = Instant.now().plusSeconds(jwtTokenProvider.getExpirationSeconds());
    Instant refreshExpiresAt = Instant.now().plusSeconds(jwtTokenProvider.getRefreshExpirationSeconds());
    JwtInformation rotatedJwtInformation = new JwtInformation(
        userDetails.getUserDto().id(),
        accessToken,
        rotatedRefreshToken,
        refreshExpiresAt
    );

    jwtRegistry.rotateJwtInformation(refreshToken, rotatedJwtInformation);

    return createIssue(userDetails, accessToken, rotatedRefreshToken, accessExpiresAt);
  }

  private JwtIssue createIssue(
      DiscodeitUserDetails userDetails,
      String accessToken,
      String refreshToken,
      Instant expiresAt
  ) {
    JwtDto body = new JwtDto(
        userDetails.getUserDto(),
        accessToken,
        TOKEN_TYPE,
        expiresAt.toString()
    );

    return new JwtIssue(body, accessToken, refreshTokenCookie(refreshToken));
  }

  private ResponseCookie refreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(JwtTokenProvider.REFRESH_TOKEN_COOKIE_NAME, refreshToken)
        .path("/")
        .httpOnly(true)
        .sameSite("Lax")
        .maxAge(jwtTokenProvider.getRefreshExpirationSeconds())
        .build();
  }
}
