package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

  private final UserDetailsService userDetailsService;
  private final byte[] secret;
  private final long accessTokenValiditySeconds;
  private final long refreshTokenValiditySeconds;

  public JwtTokenProvider(
      UserDetailsService userDetailsService,
      @Value("${discodeit.jwt.secret:default-secret-change-me}") String secret,
      @Value("${discodeit.jwt.access-validity-seconds:900}") long accessTokenValiditySeconds,
      @Value("${discodeit.jwt.refresh-validity-seconds:2592000}") long refreshTokenValiditySeconds
  ) {
    this.userDetailsService = userDetailsService;
    this.secret = secret.getBytes();
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
  }

  public String createAccessToken(UserDetails userDetails) {
    return createToken(userDetails.getUsername(),
        extractRoles(userDetails),
        accessTokenValiditySeconds,
        "access");
  }

  public String createRefreshToken(UserDetails userDetails) {
    return createToken(userDetails.getUsername(),
        List.of(),
        refreshTokenValiditySeconds,
        "refresh");
  }

  private String createToken(String subject, List<String> roles, long validitySeconds, String type) {
    try {
      Instant now = Instant.now();
      JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
          .subject(subject)
          .issueTime(Date.from(now))
          .expirationTime(Date.from(now.plusSeconds(validitySeconds)))
          .claim("typ", type);

      if (roles != null && !roles.isEmpty()) {
        claimsBuilder.claim("roles", roles);
      }

      JWTClaimsSet claims = claimsBuilder.build();
      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
      JWSSigner signer = new MACSigner(secret);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (Exception e) {
      log.error("Failed to create JWT token", e);
      throw new RuntimeException(e);
    }
  }

  public boolean validateToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWSVerifier verifier = new MACVerifier(secret);
      if (!signedJWT.verify(verifier)) {
        log.debug("JWT signature verification failed");
        return false;
      }

      Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
      if (exp == null) return false;
      if (exp.before(new Date())) {
        log.debug("JWT is expired");
        return false;
      }
      return true;
    } catch (ParseException e) {
      log.debug("Failed to parse JWT", e);
      return false;
    } catch (Exception e) {
      log.debug("JWT validation error", e);
      return false;
    }
  }

  public Authentication getAuthentication(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String username = signedJWT.getJWTClaimsSet().getSubject();
      if (username == null) return null;
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    } catch (Exception e) {
      log.debug("Failed to get authentication from token", e);
      return null;
    }
  }

  public String getSubject(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      log.debug("Failed to parse JWT", e);
      return null;
    }
  }

  private List<String> extractRoles(UserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
  }
}
