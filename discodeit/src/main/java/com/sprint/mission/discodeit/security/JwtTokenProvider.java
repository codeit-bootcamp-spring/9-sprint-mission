package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

  private final JWSSigner signer;
  private final JWSVerifier verifier;

  @Value("${discodeit.jwt.access-token.expiration-ms}")
  private long accessTokenValiditySeconds;

  @Value("${discodeit.jwt.refresh-token.expiration-ms}")
  private long refreshTokenValiditySeconds;

  public JwtTokenProvider(
      @Value("${discodeit.jwt.access-token.secret}") String secret
  ) throws JOSEException {

    byte[] keyBytes = secret.getBytes();

    this.signer = new MACSigner(keyBytes);
    this.verifier = new MACVerifier(keyBytes);

    System.out.println("[JwtTokenProvider] 초기화 완료 - 서명 키 설정됨");
  }

  public String createAccessToken(Authentication authentication) {
    try {
      String authorities = authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining(","));

      long nowSeconds = System.currentTimeMillis() / 1000;
      long expirationSeconds = nowSeconds + this.accessTokenValiditySeconds;

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(authentication.getName())
          .claim("auth", authorities)
          .issueTime(new Date(nowSeconds * 1000))
          .expirationTime(new Date(expirationSeconds * 1000))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);

      return signedJWT.serialize();
    } catch (JOSEException e) {
      log.error("Access Token 생성 중 오류가 발생했습니다.", e);
      throw new RuntimeException(e);
    }
  }

  public String createRefreshToken(Authentication authentication) {
    try {
      long nowSeconds = System.currentTimeMillis() / 1000;
      long expirationSeconds = nowSeconds + this.refreshTokenValiditySeconds;

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(authentication.getName())
          .issueTime(new Date(nowSeconds * 1000))
          .expirationTime(new Date(expirationSeconds * 1000))
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);

      return signedJWT.serialize();
    } catch (JOSEException e) {
      log.error("Refresh Token 생성 중 오류가 발생했습니다.", e);
      throw new RuntimeException(e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);

      if (!signedJWT.verify(verifier)) {
        log.info("정적 검증 실패: JWT 서명이 올바르지 않습니다.");
        return false;
      }

      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime != null && expirationTime.before(new Date())) {
        log.info("JWT 토큰이 만료되었습니다.");
        return false;
      }

      return true;
    } catch (ParseException | JOSEException e) {
      log.info("JWT 토큰 파싱 또는 검증 중 예외가 발생했습니다: {}", e.getMessage());
      return false;
    }
  }

  public JWTClaimsSet getClaims(String token) throws ParseException {
    SignedJWT signedJWT = SignedJWT.parse(token);
    return signedJWT.getJWTClaimsSet();
  }
}