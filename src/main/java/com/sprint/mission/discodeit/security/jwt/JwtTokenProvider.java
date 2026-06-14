package com.sprint.mission.discodeit.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

  private final DiscodeitUserDetailsService userDetailsService;

  private final byte[] secret;
  private final long accessTokenValiditySeconds;
  private final long refreshTokenValiditySeconds;

  public JwtTokenProvider(
      DiscodeitUserDetailsService userDetailsService,
      @Value("${discodeit.jwt.secret:default-secret-change-me}") String secret,
      @Value("${discodeit.jwt.access-token-expire-seconds:3600}") long accessTokenValiditySeconds,
      @Value("${discodeit.jwt.refresh-token-expire-seconds:1209600}") long refreshTokenValiditySeconds
  ) {
    this.userDetailsService = userDetailsService;
    this.secret = secret.getBytes(StandardCharsets.UTF_8);
    this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
  }

  public String generateAccessToken(UserDto userDto) {
    return createToken(
        userDto.id().toString(),
        userDto.username(),
        extractRoles(userDto),
        accessTokenValiditySeconds,
        "access"
    );
  }

  public String generateRefreshToken(UUID userId) {
    // backward compatible: generate without jti (not recommended)
    return generateRefreshToken(userId, UUID.randomUUID());
  }

  public String generateRefreshToken(UUID userId, UUID jti) {
    return createToken(
        userId.toString(),
        null,
        List.of(),
        refreshTokenValiditySeconds,
        "refresh",
        jti
    );
  }

  public String createAccessToken(UserDetails userDetails) {
    return createToken(
        userDetails.getUsername(),
        userDetails.getUsername(),
        extractRoles(userDetails),
        accessTokenValiditySeconds,
        "access"
    );
  }

  public String createRefreshToken(UserDetails userDetails) {
    return createToken(
        userDetails.getUsername(),
        null,
        List.of(),
        refreshTokenValiditySeconds,
        "refresh"
    );
  }

  private String createToken(String subject, String username, List<String> roles, long validitySeconds, String type) {
    return createToken(subject, username, roles, validitySeconds, type, null);
  }

  private String createToken(String subject, String username, List<String> roles, long validitySeconds, String type, UUID jti) {
    try {
      Instant now = Instant.now();
      JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
          .subject(subject)
          .issueTime(Date.from(now))
          .expirationTime(Date.from(now.plusSeconds(validitySeconds)))
          .claim("type", type);

      if (username != null) {
        claimsBuilder.claim("username", username);
      }
      if (jti != null) {
        claimsBuilder.claim("jti", jti.toString());
      }
      if (roles != null && !roles.isEmpty()) {
        claimsBuilder.claim("roles", roles);
      }

      JWTClaimsSet claims = claimsBuilder.build();
      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader.Builder(JWSAlgorithm.HS256)
              .type(JOSEObjectType.JWT)
              .build(),
          claims
      );
      JWSSigner signer = new MACSigner(secret);
      signedJWT.sign(signer);
      return signedJWT.serialize();
    } catch (JOSEException e) {
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
    } catch (JOSEException e) {
      log.debug("JWT validation error", e);
      return false;
    }
  }

  public boolean isRefreshToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String type = (String) signedJWT.getJWTClaimsSet().getClaim("type");
      return "refresh".equalsIgnoreCase(type);
    } catch (ParseException e) {
      log.debug("Failed to parse JWT", e);
      return false;
    }
  }

  public UUID getJti(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String jti = (String) signedJWT.getJWTClaimsSet().getClaim("jti");
      return jti != null ? UUID.fromString(jti) : null;
    } catch (ParseException e) {
      log.debug("Failed to parse JWT jti", e);
      return null;
    }
  }

  public Authentication getAuthentication(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String username = (String) signedJWT.getJWTClaimsSet().getClaim("username");
      if (username == null) {
        username = signedJWT.getJWTClaimsSet().getSubject();
      }
      if (username == null) return null;
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    } catch (Exception e) {
      log.debug("Failed to get authentication from token", e);
      return null;
    }
  }

  public UUID getUserId(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      return UUID.fromString(signedJWT.getJWTClaimsSet().getSubject());
    } catch (ParseException e) {
      log.debug("Failed to parse JWT", e);
      return null;
    }
  }

  public String getUsername(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String username = (String) signedJWT.getJWTClaimsSet().getClaim("username");
      return username != null ? username : signedJWT.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      log.debug("Failed to parse JWT", e);
      return null;
    }
  }

  public long getRefreshTokenValiditySeconds() {
    return refreshTokenValiditySeconds;
  }

  public long getAccessTokenValiditySeconds() {
    return accessTokenValiditySeconds;
  }

  private List<String> extractRoles(UserDetails userDetails) {
    return userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toList());
  }

  private List<String> extractRoles(UserDto userDto) {
    return List.of("ROLE_" + userDto.role().name());
  }
}
