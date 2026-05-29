package com.sprint.mission.discodeit.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final ObjectMapper objectMapper;

  @Value("${jwt.access-token-validity-seconds}")
  private long accessTokenValiditySeconds;
  @Value("${jwt.refresh-token-validity-seconds}")
  private long refreshTokenValiditySeconds;

  public JwtTokenProvider(@Value("${jwt.secret}") String secret,
      ObjectMapper objectMapper) throws JOSEException {
    byte[] keyBytes = Base64.getDecoder().decode(secret);
    this.signer = new MACSigner(keyBytes);
    this.verifier = new MACVerifier(keyBytes);
    this.objectMapper = objectMapper;
  }

  public String createAccessToken(Authentication authentication) {
    try {
      String authorities = authentication.getAuthorities().stream()
          .map(GrantedAuthority::getAuthority)
          .collect(Collectors.joining(","));
      Date now = new Date();
      Date validity = new Date(now.getTime() + (this.accessTokenValiditySeconds * 1000));

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(authentication.getName())
          .claim("authorities", authorities)
          .issueTime(now)
          .expirationTime(validity)
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("토큰 생성 중 서버 오류가 발생했습니다.", e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      if (!signedJWT.verify(verifier)) {
        log.info("JWT 서명이 일치하지 않습니다.(위조 위험).");
        return false;
      }
      Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (expirationTime.before(new Date())) {
        log.info("만료된 JWT 토큰입니다.");
        return false;
      }
      return true;
    } catch (ParseException | JOSEException e) {
      log.info("잘못된 규격의 JWT 토큰입니다.");
      return false;
    }
  }

  public Authentication getAuthentication(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      Object authoritiesClaim = claimsSet.getClaim("authorities");
      Collection<? extends GrantedAuthority> authorities = (authoritiesClaim == null)
          ? Collections.emptyList()
          : Arrays.stream(authoritiesClaim.toString().split(","))
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());

      UserDetails principal = new User(claimsSet.getSubject(), "", authorities);
      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    } catch (ParseException e) {
      throw new RuntimeException("토큰 해석 중 오류가 발생했습니다.", e);
    }
  }

  public String createRefreshToken(Authentication authentication) {
    try {
      Date now = new Date();
      // 설정 파일에 정의된 리프레시 토큰 유효시간 사용
      Date validity = new Date(now.getTime() + (this.refreshTokenValiditySeconds * 1000));

      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(authentication.getName())
          .issueTime(now)
          .expirationTime(validity)
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new RuntimeException("리프레시 토큰 생성 중 서버 오류가 발생했습니다.", e);
    }
  }

  public java.time.LocalDateTime getExpiration(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

      return expiration.toInstant()
          .atZone(java.time.ZoneId.systemDefault())
          .toLocalDateTime();
    } catch (ParseException e) {
      throw new RuntimeException("토큰에서 만료 시간을 추출할 수 없습니다.", e);
    }
  }

}
