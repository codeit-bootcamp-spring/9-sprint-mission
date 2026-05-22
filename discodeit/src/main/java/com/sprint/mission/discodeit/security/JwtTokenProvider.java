package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final byte[] secret;
  private final long expirationSeconds;

  public JwtTokenProvider(
      @Value("${discodeit.security.token.secret}") String secret,
      @Value("${discodeit.security.token.expiration-seconds}") long expirationSeconds
  ) {
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    if (this.secret.length < 32) {
      throw new IllegalArgumentException("JWT HS256 secret must be at least 32 bytes");
    }
    this.expirationSeconds = expirationSeconds;
  }

  public String createToken(DiscodeitUserDetails userDetails) {
    Instant issuedAt = Instant.now();
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userDetails.getUsername())
        .claim("uid", userDetails.getUserDto().id().toString())
        .jwtID(UUID.randomUUID().toString())
        .issueTime(Date.from(issuedAt))
        .expirationTime(Date.from(issuedAt.plusSeconds(expirationSeconds)))
        .build();

    return sign(claims);
  }

  public String refreshToken(DiscodeitUserDetails userDetails) {
    return createToken(userDetails);
  }

  public boolean validateToken(String token) {
    SignedJWT signedJWT = parse(token);
    verifySignature(signedJWT);
    Date expirationTime = getClaims(signedJWT).getExpirationTime();
    return expirationTime != null && expirationTime.after(new Date());
  }

  public String getUsername(String token) {
    if (!validateToken(token)) {
      throw new BadCredentialsException("Invalid token");
    }

    String username = getClaims(parse(token)).getSubject();
    if (username == null || username.isBlank()) {
      throw new BadCredentialsException("Token subject is missing");
    }
    return username;
  }

  public long getExpirationSeconds() {
    return expirationSeconds;
  }

  private String sign(JWTClaimsSet claims) {
    try {
      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader.Builder(JWSAlgorithm.HS256).type(com.nimbusds.jose.JOSEObjectType.JWT)
              .build(),
          claims
      );
      signedJWT.sign(new MACSigner(secret));
      return signedJWT.serialize();
    } catch (JOSEException e) {
      throw new IllegalStateException("Failed to sign JWT", e);
    }
  }

  private SignedJWT parse(String token) {
    try {
      return SignedJWT.parse(token);
    } catch (ParseException e) {
      throw new BadCredentialsException("Invalid token format", e);
    }
  }

  private JWTClaimsSet getClaims(SignedJWT signedJWT) {
    try {
      return signedJWT.getJWTClaimsSet();
    } catch (ParseException e) {
      throw new BadCredentialsException("Invalid token claims", e);
    }
  }

  private void verifySignature(SignedJWT signedJWT) {
    try {
      if (!signedJWT.verify(new MACVerifier(secret))) {
        throw new BadCredentialsException("Invalid token signature");
      }
    } catch (JOSEException e) {
      throw new BadCredentialsException("Invalid token signature", e);
    }
  }
}
