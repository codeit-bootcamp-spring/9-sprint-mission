package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.JwtDto;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtTokenIssuer {

  public static final String TOKEN_TYPE = "Bearer";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  public JwtIssue issue(DiscodeitUserDetails userDetails) {
    String accessToken = jwtTokenProvider.createToken(userDetails);
    String refreshToken = jwtTokenProvider.refreshToken(userDetails);
    Instant expiresAt = Instant.now().plusSeconds(jwtTokenProvider.getExpirationSeconds());
    JwtInformation jwtInformation = new JwtInformation(
        userDetails.getUserDto().id(),
        accessToken,
        refreshToken,
        expiresAt
    );

    jwtRegistry.registerJwtInformation(jwtInformation);

    return createIssue(userDetails, accessToken, refreshToken, expiresAt);
  }

  public JwtIssue rotate(String refreshToken, DiscodeitUserDetails userDetails) {
    String accessToken = jwtTokenProvider.createToken(userDetails);
    String rotatedRefreshToken = jwtTokenProvider.refreshToken(userDetails);
    Instant expiresAt = Instant.now().plusSeconds(jwtTokenProvider.getExpirationSeconds());
    JwtInformation rotatedJwtInformation = new JwtInformation(
        userDetails.getUserDto().id(),
        accessToken,
        rotatedRefreshToken,
        expiresAt
    );

    jwtRegistry.rotateJwtInformation(refreshToken, rotatedJwtInformation);

    return createIssue(userDetails, accessToken, rotatedRefreshToken, expiresAt);
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
        .maxAge(jwtTokenProvider.getExpirationSeconds())
        .build();
  }
}
