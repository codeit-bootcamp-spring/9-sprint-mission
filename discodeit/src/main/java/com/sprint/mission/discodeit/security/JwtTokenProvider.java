package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";
  private static final String CLAIM_USERNAME = "username";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TOKEN_TYPE = "tokenType";

  private final String secretKey;
  private final long accessTokenExpiration;
  private final long refreshTokenExpiration;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.secret}") String secretKey,
      @Value("${discodeit.jwt.access-token-expiration:3600000}") long accessTokenExpiration,
      @Value("${discodeit.jwt.refresh-token-expiration:1209600000}") long refreshTokenExpiration
  ) {
    this.secretKey = secretKey;
    this.accessTokenExpiration = accessTokenExpiration;
    this.refreshTokenExpiration = refreshTokenExpiration;
  }

  public String generateAccessToken(UUID userId, String username, String role) {
    return generateToken(userId, username, role, accessTokenExpiration, TOKEN_TYPE_ACCESS);
  }

  public String generateRefreshToken(UUID userId, String username, String role) {
    return generateToken(userId, username, role, refreshTokenExpiration, TOKEN_TYPE_REFRESH);
  }

  public String refreshAccessToken(String refreshToken) {
    validateTokenOrThrow(refreshToken, TOKEN_TYPE_REFRESH);
    JWTClaimsSet claims = getClaims(refreshToken);
    return generateAccessToken(
        UUID.fromString(claims.getSubject()),
        (String) claims.getClaim(CLAIM_USERNAME),
        (String) claims.getClaim(CLAIM_ROLE)
    );
  }

  public void validateTokenOrThrow(String token, String expectedTokenType) {
    if (!validateToken(token)) {
      throw new JwtAuthenticationException("유효하지 않은 토큰입니다.");
    }
    JWTClaimsSet claims = getClaims(token);
    String actualType = (String) claims.getClaim(CLAIM_TOKEN_TYPE);
    if (!expectedTokenType.equals(actualType)) {
      throw new JwtAuthenticationException(
          expectedTokenType + " 토큰이 아닙니다. 실제 타입: " + actualType);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey.getBytes());
      if (!signedJWT.verify(verifier)) {
        log.warn("JWT 서명 검증 실패");
        return false;
      }
      Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expiration == null || expiration.before(new Date())) {
        log.warn("JWT 토큰 만료: {}", expiration);
        return false;
      }
      return true;
    } catch (ParseException e) {
      log.warn("JWT 파싱 오류: {}", e.getMessage());
      return false;
    } catch (JOSEException e) {
      log.warn("JWT 서명 검증 오류: {}", e.getMessage());
      return false;
    }
  }

  public boolean validateAccessToken(String token) {
    return validateToken(token);
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token);
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getClaims(token).getSubject());
  }

  public String getUsername(String token) {
    return (String) getClaims(token).getClaim(CLAIM_USERNAME);
  }

  public String getRole(String token) {
    return (String) getClaims(token).getClaim(CLAIM_ROLE);
  }

  private String generateToken(UUID userId, String username, String role,
      long expirationMs, String tokenType) {
    try {
      Date now = new Date();
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
          .subject(userId.toString())
          .claim(CLAIM_USERNAME, username)
          .claim(CLAIM_ROLE, role)
          .claim(CLAIM_TOKEN_TYPE, tokenType)
          .issueTime(now)
          .expirationTime(new Date(now.getTime() + expirationMs))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      signedJWT.sign(new MACSigner(secretKey.getBytes()));
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new JwtAuthenticationException("JWT 토큰 생성 실패", e);
    }
  }

  private JWTClaimsSet getClaims(String token) {
    try {
      return SignedJWT.parse(token).getJWTClaimsSet();
    } catch (ParseException e) {
      throw new JwtAuthenticationException("JWT 파싱 실패", e);
    }
  }
}