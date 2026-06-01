package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private final byte[] secretKey;
  private final long accessTokenExpiry;
  private final long refreshTokenExpiry;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expiry}") long accessTokenExpiry,
      @Value("${jwt.refresh-token-expiry}") long refreshTokenExpiry
  ) {
    this.secretKey = secret.getBytes();
    this.accessTokenExpiry = accessTokenExpiry;
    this.refreshTokenExpiry = refreshTokenExpiry;
  }

  public String generateAccessToken(UserDto userDto) {
    return generateToken(userDto, accessTokenExpiry);
  }

  public String generateRefreshToken(UserDto userDto) {
    return generateToken(userDto, refreshTokenExpiry);
  }

  private String generateToken(UserDto userDto, long expiry) {
    Date now = new Date();
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userDto.id().toString())
        .claim("username", userDto.username())
        .claim("role", userDto.role().name())
        .issueTime(now)
        .expirationTime(new Date(now.getTime() + expiry))
        .build();

    try {
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      signedJWT.sign(new MACSigner(secretKey));
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("JWT 생성 실패", e);
    }
  }

  public JWTClaimsSet validateAndGetClaims(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secretKey);

      if (!signedJWT.verify(verifier)) {
        throw new RuntimeException("유효하지 않은 JWT 서명");
      }

      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      if (claims.getExpirationTime() != null && claims.getExpirationTime().before(new Date())) {
        throw new RuntimeException("만료된 JWT");
      }

      return claims;
    } catch (ParseException | JOSEException e) {
      throw new RuntimeException("JWT 검증 실패", e);
    }
  }

  public UUID extractUserId(JWTClaimsSet claims) {
    return UUID.fromString(claims.getSubject());
  }

  public String extractUsername(JWTClaimsSet claims) {
    try {
      return claims.getStringClaim("username");
    } catch (ParseException e) {
      throw new RuntimeException("JWT 클레임 파싱 실패", e);
    }
  }

  public Role extractRole(JWTClaimsSet claims) {
    try {
      return Role.valueOf(claims.getStringClaim("role"));
    } catch (ParseException e) {
      throw new RuntimeException("JWT 클레임 파싱 실패", e);
    }
  }
}
