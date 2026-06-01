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

  // Access Token 발급
  public String generateAccessToken(UUID userId, String username, String role) {
    return generateToken(userId, username, role, accessTokenExpiration, "access");
  }

  // Refresh Token 발급
  public String generateRefreshToken(UUID userId, String username, String role) {
    return generateToken(userId, username, role, refreshTokenExpiration, "refresh");
  }

  // Refresh Token → 새 Access Token 갱신
  public String refreshAccessToken(String refreshToken) {
    if (!validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
    }
    JWTClaimsSet claims = getClaims(refreshToken);
    if (!"refresh".equals(claims.getClaim("tokenType"))) {
      throw new IllegalArgumentException("Refresh Token이 아닙니다.");
    }
    return generateAccessToken(
        UUID.fromString(claims.getSubject()),
        (String) claims.getClaim("username"),
        (String) claims.getClaim("role")
    );
  }

  // 서명 + 만료 시간 유효성 검사
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
    } catch (ParseException | JOSEException e) {
      log.warn("JWT 검증 오류: {}", e.getMessage());
      return false;
    }
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getClaims(token).getSubject());
  }

  public String getUsername(String token) {
    return (String) getClaims(token).getClaim("username");
  }

  public String getRole(String token) {
    return (String) getClaims(token).getClaim("role");
  }

  // 공통 토큰 생성
  private String generateToken(UUID userId, String username, String role,
      long expirationMs, String tokenType) {
    try {
      Date now = new Date();
      JWTClaimsSet claims = new JWTClaimsSet.Builder()
          .subject(userId.toString())
          .claim("username", username)
          .claim("role", role)
          .claim("tokenType", tokenType)
          .issueTime(now)
          .expirationTime(new Date(now.getTime() + expirationMs))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      signedJWT.sign(new MACSigner(secretKey.getBytes()));
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("JWT 토큰 생성 실패", e);
    }
  }

  private JWTClaimsSet getClaims(String token) {
    try {
      return SignedJWT.parse(token).getJWTClaimsSet();
    } catch (ParseException e) {
      throw new IllegalArgumentException("JWT 파싱 실패", e);
    }
  }
}
