package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final int accessTokenExpirationMs;
  private final int refreshTokenExpirationMs;

  private final JWSSigner accessTokenSigner;
  private final JWSVerifier accessTokenVerifier;
  private final JWSSigner refreshTokenSigner;
  private final JWSVerifier refreshTokenVerifier;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.access-token.secret}") String accessTokenSecret,
      @Value("${discodeit.jwt.access-token.expiration-ms}") int accessTokenExpirationMs,
      @Value("${discodeit.jwt.refresh-token.secret}") String refreshTokenSecret,
      @Value("${discodeit.jwt.refresh-token.expiration-ms}") int refreshTokenExpirationMs)
      throws JOSEException {

    this.accessTokenExpirationMs = accessTokenExpirationMs;
    this.refreshTokenExpirationMs = refreshTokenExpirationMs;

    byte[] accessSecretBytes = accessTokenSecret.getBytes(StandardCharsets.UTF_8);
    this.accessTokenSigner = new MACSigner(accessSecretBytes);
    this.accessTokenVerifier = new MACVerifier(accessSecretBytes);

    byte[] refreshSecretBytes = refreshTokenSecret.getBytes(StandardCharsets.UTF_8);
    this.refreshTokenSigner = new MACSigner(refreshSecretBytes);
    this.refreshTokenVerifier = new MACVerifier(refreshSecretBytes);
  }

  public String generateAccessToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, accessTokenExpirationMs, accessTokenSigner, "access");
  }

  public String generateRefreshToken(DiscodeitUserDetails userDetails) throws JOSEException {
    return generateToken(userDetails, refreshTokenExpirationMs, refreshTokenSigner, "refresh");
  }

  private String generateToken(DiscodeitUserDetails userDetails, int expirationMs, JWSSigner signer,
      String tokenType) throws JOSEException {
    String tokenId = UUID.randomUUID().toString();
    UserDto user = userDetails.getUserDto();

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMs);

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(user.username())
        .jwtID(tokenId)
        .claim("userId", user.id().toString())
        .claim("type", tokenType)
        .claim("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()))
        .issueTime(now)
        .expirationTime(expiryDate)
        .build();

    SignedJWT signedJWT = new SignedJWT(
        new JWSHeader(JWSAlgorithm.HS256),
        claimsSet
    );

    signedJWT.sign(signer);
    String token = signedJWT.serialize();

    log.debug("Generated {} token for user: {}", tokenType, user.username());
    return token;
  }

  public boolean validateAccessToken(String token) {
    return validateToken(token, accessTokenVerifier, "access");
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token, refreshTokenVerifier, "refresh");
  }

  private boolean validateToken(String token, JWSVerifier verifier, String expectedType) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      // Verify signature
      if (!signedJWT.verify(verifier)) {
        log.debug("JWT signature verification failed for {} token", expectedType);
        return false;
      }

      // Check token type
      String tokenType = (String) signedJWT.getJWTClaimsSet().getClaim("type");
      if (!expectedType.equals(tokenType)) {
        log.debug("JWT token type mismatch: expected {}, got {}", expectedType, tokenType);
        return false;
      }

      // Check expiration
      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null || expirationTime.before(new Date())) {
        log.debug("JWT {} token expired", expectedType);
        return false;
      }

      return true;
    } catch (Exception e) {
      log.debug("JWT {} token validation failed: {}", expectedType, e.getMessage());
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  public String getTokenId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getJWTID();
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  public UUID getUserId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String userIdStr = (String) signedJWT.getJWTClaimsSet().getClaim("userId");
      if (userIdStr == null) {
        throw new IllegalArgumentException("User ID claim not found in JWT token");
      }
      return UUID.fromString(userIdStr);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }

  public Cookie genereateRefreshTokenCookie(String refreshToken) {
    // Set refresh token in HttpOnly cookie
    Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true); // Use HTTPS in production
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(refreshTokenExpirationMs / 1000);
    return refreshCookie;
  }

  public Cookie genereateRefreshTokenExpirationCookie() {
    Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true); // Use HTTPS in production
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(0);
    return refreshCookie;
  }
}