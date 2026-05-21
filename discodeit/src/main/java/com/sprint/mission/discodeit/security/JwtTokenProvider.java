package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final String CLAIM_USER_ID = "userId";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TOKEN_TYPE = "tokenType";
  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final byte[] secret;
  private final long accessTokenValiditySeconds;
  private final long refreshTokenValiditySeconds;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.secret:discodeit-mission-10-default-jwt-secret-key}") String secret,
      @Value("${discodeit.jwt.access-token-validity-seconds:1800}")
      long accessTokenValiditySeconds,
      @Value("${discodeit.jwt.refresh-token-validity-seconds:1209600}")
      long refreshTokenValiditySeconds
  ) {
    if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
      throw new IllegalArgumentException("JWT secret must be at least 256 bits");
    }
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
  }

  public String generateAccessToken(DiscodeitUserDetails userDetails) {
    return generateAccessToken(userDetails.getUserDto());
  }

  public String generateAccessToken(UserDto userDto) {
    return generateToken(userDto, TOKEN_TYPE_ACCESS, accessTokenValiditySeconds);
  }

  public String generateRefreshToken(DiscodeitUserDetails userDetails) {
    return generateRefreshToken(userDetails.getUserDto());
  }

  public String generateRefreshToken(UserDto userDto) {
    return generateToken(userDto, TOKEN_TYPE_REFRESH, refreshTokenValiditySeconds);
  }

  public String refreshAccessToken(String refreshToken) {
    JWTClaimsSet claims = getClaims(refreshToken);
    if (!TOKEN_TYPE_REFRESH.equals(getTokenType(claims))) {
      throw new IllegalArgumentException("Refresh token is required");
    }
    UserDto userDto = new UserDto(
        UUID.fromString(getStringClaim(claims, CLAIM_USER_ID)),
        claims.getSubject(),
        null,
        null,
        null,
        Role.valueOf(getStringClaim(claims, CLAIM_ROLE))
    );
    return generateAccessToken(userDto);
  }

  public boolean validateToken(String token) {
    try {
      getClaims(token);
      return true;
    } catch (RuntimeException exception) {
      return false;
    }
  }

  public JWTClaimsSet getClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      if (!signedJWT.verify(new MACVerifier(secret))) {
        throw new IllegalArgumentException("Invalid JWT signature");
      }
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      Date expirationTime = claims.getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        throw new IllegalArgumentException("Expired JWT");
      }
      return claims;
    } catch (ParseException | JOSEException exception) {
      throw new IllegalArgumentException("Invalid JWT", exception);
    }
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getStringClaim(getClaims(token), CLAIM_USER_ID));
  }

  public String getUsername(String token) {
    return getClaims(token).getSubject();
  }

  public Role getRole(String token) {
    return Role.valueOf(getStringClaim(getClaims(token), CLAIM_ROLE));
  }

  private String generateToken(UserDto userDto, String tokenType, long validitySeconds) {
    try {
      Instant issuedAt = Instant.now();
      Instant expiresAt = issuedAt.plusSeconds(validitySeconds);
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
          .subject(userDto.username())
          .issueTime(Date.from(issuedAt))
          .expirationTime(Date.from(expiresAt))
          .claim(CLAIM_USER_ID, userDto.id().toString())
          .claim(CLAIM_ROLE, userDto.role().name())
          .claim(CLAIM_TOKEN_TYPE, tokenType)
          .build();
      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader(JWSAlgorithm.HS256),
          claims
      );
      signedJWT.sign(new MACSigner(secret));
      return signedJWT.serialize();
    } catch (JOSEException exception) {
      throw new IllegalStateException("Failed to generate JWT", exception);
    }
  }

  private String getTokenType(JWTClaimsSet claims) {
    return getStringClaim(claims, CLAIM_TOKEN_TYPE);
  }

  private String getStringClaim(JWTClaimsSet claims, String claimName) {
    try {
      return claims.getStringClaim(claimName);
    } catch (ParseException exception) {
      throw new IllegalArgumentException("Invalid JWT claim: " + claimName, exception);
    }
  }
}
