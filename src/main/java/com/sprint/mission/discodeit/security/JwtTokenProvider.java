package com.sprint.mission.discodeit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 발급 / 갱신 / 유효성 검사를 담당하는 컴포넌트
 */
@Component
public class JwtTokenProvider {

  private final Key key;
  private final long accessTokenExpireMs;
  private final long refreshTokenExpireMs;

  // application.yml 의 값을 생성자에서 주입받음
  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-expire-ms}") long accessTokenExpireMs,
      @Value("${jwt.refresh-token-expire-ms}") long refreshTokenExpireMs) {

    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.accessTokenExpireMs = accessTokenExpireMs;
    this.refreshTokenExpireMs = refreshTokenExpireMs;
  }

  // ------------------------------------------------------------------
  // 1. 토큰 발급
  // ------------------------------------------------------------------

  /**
   * Access Token 발급
   * - subject: 누구의 토큰인지 (보통 userId 또는 email)
   */
  public String createAccessToken(String subject) {
    return createToken(subject, accessTokenExpireMs);
  }

  /**
   * Refresh Token 발급
   * - Access Token이 만료됐을 때 재발급용으로 사용
   */
  public String createRefreshToken(String subject) {
    return createToken(subject, refreshTokenExpireMs);
  }

  // ------------------------------------------------------------------
  // 2. 토큰 갱신
  // ------------------------------------------------------------------

  /**
   * Refresh Token을 받아서 새로운 Access Token을 반환
   * - Refresh Token도 만료되면 재로그인 필요
   */
  public String renewAccessToken(String refreshToken) {
    if (!isValidToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다. 다시 로그인해주세요.");
    }

    // 기존 토큰에서 subject(사용자 정보) 꺼내서 새 Access Token 발급
    String subject = getSubject(refreshToken);
    return createAccessToken(subject);
  }

  // ------------------------------------------------------------------
  // 3. 유효성 검사
  // ------------------------------------------------------------------

  /**
   * 토큰이 유효한지 확인 (서명 검증 + 만료 여부)
   * - 유효하면 true, 아니면 false 반환
   */
  public boolean isValidToken(String token) {
    try {
      getClaims(token); // 파싱 성공 = 유효한 토큰
      return true;
    } catch (ExpiredJwtException e) {
      System.out.println("만료된 토큰: " + e.getMessage());
    } catch (JwtException e) {
      System.out.println("유효하지 않은 토큰: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("토큰이 비어있음: " + e.getMessage());
    }
    return false;
  }

  // ------------------------------------------------------------------
  // 4. 토큰에서 정보 꺼내기
  // ------------------------------------------------------------------

  /**
   * 토큰에서 subject(사용자 식별자) 추출
   */
  public String getSubject(String token) {
    return getClaims(token).getSubject();
  }

  /**
   * 토큰 만료 시각 반환
   */
  public Date getExpiration(String token) {
    return getClaims(token).getExpiration();
  }

  // ------------------------------------------------------------------
  // 내부 헬퍼 메서드
  // ------------------------------------------------------------------

  /**
   * 공통 토큰 생성 로직
   */
  private String createToken(String subject, long expireMs) {
    Date now    = new Date();
    Date expiry = new Date(now.getTime() + expireMs);

    return Jwts.builder()
        .setSubject(subject)                      // 사용자 식별자
        .setIssuedAt(now)                         // 발급 시각
        .setExpiration(expiry)                    // 만료 시각
        .signWith(key, SignatureAlgorithm.HS256)  // 서명 알고리즘
        .compact();
  }

  /**
   * 토큰을 파싱해서 Claims(페이로드) 반환
   * - 서명이 틀리거나 만료되면 JwtException 발생
   */
  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}