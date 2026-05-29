package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

  public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

  private static final String TOKEN_TYPE_CLAIM = "tokenType";
  private static final String ACCESS_TOKEN_TYPE = "ACCESS";
  private static final String REFRESH_TOKEN_TYPE = "REFRESH";

  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final long accessTokenExpirationPeriod;
  private final long refreshTokenExpirationPeriod;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secretKey,
      @Value("${jwt.access-token-expiration}") long accessTokenExpirationPeriod,
      @Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationPeriod) {

    try {

      byte[] secretKeyBytes = secretKey.getBytes();
      this.signer = new MACSigner(secretKeyBytes);
      this.verifier = new MACVerifier(secretKeyBytes);
    } catch (JOSEException e) {
      log.error("JWT 시크릿 키 설정 오류가 발생했습니다.", e);
      throw new RuntimeException("JWT 시크릿 키 설정 실패: " + e.getMessage(), e);
    }

    this.accessTokenExpirationPeriod = accessTokenExpirationPeriod;
    this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod;
  }

  public String generateToken(Authentication authentication, long expirationPeriod) {
    return generateToken(authentication, expirationPeriod, ACCESS_TOKEN_TYPE);
  }

  private String generateToken(Authentication authentication, long expirationPeriod, String tokenType) {
    // 권한 정보 가져오기
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    Date now = new Date();
    Date expirationTime = new Date(now.getTime() + expirationPeriod);

    // JWT Payload(클레임) 설정
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(authentication.getName())
        .claim("auth", authorities)
        .claim(TOKEN_TYPE_CLAIM, tokenType)
        .issueTime(now)
        .expirationTime(expirationTime)
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

    try {
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      log.error("JWT 서명 중 에러가 발생했습니다.", e);
      throw new RuntimeException("JWT 토큰 생성 실패");
    }
  }

  // Access Token 생성 편의 메서드
  public String createAccessToken(Authentication authentication) {
    return generateToken(authentication, accessTokenExpirationPeriod, ACCESS_TOKEN_TYPE);
  }

  // Refresh Token 생성 편의 메서드
  public String createRefreshToken(Authentication authentication) {
    return generateToken(authentication, refreshTokenExpirationPeriod, REFRESH_TOKEN_TYPE);
  }


  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        log.warn("잘못된 JWT 서명입니다.");
        return false;
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime != null && new Date().after(expirationTime)) {
        log.warn("만료된 JWT 토큰입니다.");
        return false;
      }

      return true;
    } catch (ParseException e) {
      log.warn("지원되지 않는 형식이거나 잘못된 JWT 토큰입니다.");
    } catch (JOSEException e) {
      log.warn("JWT 토큰 검증 중 에러가 발생했습니다.");
    }
    return false;
  }

  public boolean validateAccessToken(String token) {
    return validateToken(token) && ACCESS_TOKEN_TYPE.equals(getTokenType(token));
  }

  public boolean validateRefreshToken(String token) {
    return validateToken(token) && REFRESH_TOKEN_TYPE.equals(getTokenType(token));
  }

  public Authentication getAuthentication(String token) {
    try {
      if (!validateAccessToken(token)) {
        throw new RuntimeException("유효하지 않은 엑세스 토큰입니다.");
      }

      SignedJWT signedJWT = SignedJWT.parse(token);
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

      String authClaim = claims.getStringClaim("auth");
      if (authClaim == null || authClaim.trim().isEmpty()) {
        throw new RuntimeException("권한 정보가 없는 토큰입니다.");
      }

      Collection<? extends GrantedAuthority> authorities = Arrays.stream(authClaim.split(","))
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());

      UserDetails principal = new User(claims.getSubject(), "", authorities);
      return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    } catch (ParseException e) {
      throw new RuntimeException("토큰 파싱 중 에러가 발생했습니다.");
    }
  }

  private String getTokenType(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getStringClaim(TOKEN_TYPE_CLAIM);
    } catch (ParseException e) {
      return null;
    }
  }

  public Instant getExpirationTime(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime == null) {
        throw new RuntimeException("토큰 만료 시간이 없습니다.");
      }
      return expirationTime.toInstant();
    } catch (ParseException e) {
      throw new RuntimeException("토큰에서 만료 시간을 추출할 수 없습니다.");
    }
  }

  public String getUserIdFromToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      throw new RuntimeException("토큰에서 유저 정보를 추출할 수 없습니다.");
    }
  }
}
