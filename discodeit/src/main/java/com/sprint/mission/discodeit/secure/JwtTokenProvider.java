package com.sprint.mission.discodeit.secure;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.exception.secure.JwtAuthenticationException;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

  private final MACSigner signer;

  private final MACVerifier verifier;

  private final long accessTokenValiditySeconds;

  private final long refreshTokenValiditySeconds;

  public JwtTokenProvider(
      @Value("${jwt.secret}")
      String secret,

      @Value("${jwt.access-token-validity-seconds}")
      long accessTokenValiditySeconds,

      @Value("${jwt.refresh-token-validity-seconds}")
      long refreshTokenValiditySeconds
  ) throws JOSEException {

    byte[] secretKey =
        Base64.getDecoder().decode(secret);

    this.signer =
        new MACSigner(secretKey);

    this.verifier =
        new MACVerifier(secretKey);

    this.accessTokenValiditySeconds =
        accessTokenValiditySeconds;

    this.refreshTokenValiditySeconds =
        refreshTokenValiditySeconds;

    log.info(
        "JwtTokenProvider initialized"
    );
  }

  public String generateAccessToken(
      UserDetails userDetails
  ) {

    try {

      Instant now = Instant.now();

      Instant expiration =
          now.plusSeconds(
              accessTokenValiditySeconds
          );

      List<String> authorities =
          userDetails.getAuthorities()
              .stream()
              .map(
                  GrantedAuthority::getAuthority
              )
              .toList();

      JWTClaimsSet claimsSet =
          new JWTClaimsSet.Builder()

              .subject(
                  userDetails.getUsername()
              )

              .issueTime(
                  Date.from(now)
              )

              .expirationTime(
                  Date.from(expiration)
              )

              .jwtID(
                  UUID.randomUUID()
                      .toString()
              )

              .claim(
                  "authorities",
                  authorities
              )

              .build();

      SignedJWT signedJWT =
          new SignedJWT(
              new JWSHeader(
                  JWSAlgorithm.HS256
              ),
              claimsSet
          );

      signedJWT.sign(signer);

      return signedJWT.serialize();

    } catch (Exception e) {

      throw new JwtAuthenticationException("JWT 생성 실패", e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      if (!signedJWT.verify(verifier)) {
        return false;
      }
      Date expirationTime =
          signedJWT.getJWTClaimsSet().getExpirationTime();
      return expirationTime.after(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public String getUsername(String token) {
    try {
      SignedJWT signedJWT =
          SignedJWT.parse(token);
      return signedJWT
          .getJWTClaimsSet()
          .getSubject();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  public String generateRefreshToken() {

    return UUID.randomUUID()
        + "-"
        + UUID.randomUUID()
        .toString()
        .replace("-", "");
  }

  public long getRefreshTokenValiditySeconds() {
    return refreshTokenValiditySeconds;
  }
}

