package com.sprint.mission.discodeit.security.jwt.store;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository repository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public String createRefreshToken(UUID userId) {
    UUID jti = UUID.randomUUID();
    String token = jwtTokenProvider.generateRefreshToken(userId, jti);

    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(jwtTokenProvider.getRefreshTokenValiditySeconds());

    RefreshToken entity = new RefreshToken(jti, userId, now, expiresAt, false, null);
    repository.save(entity);
    return token;
  }

  @Transactional
  public String rotateRefreshToken(UUID oldJti, UUID userId) {
    Optional<RefreshToken> existing = repository.findById(oldJti);
    if (existing.isEmpty()) {
      throw new DiscodeitException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
    RefreshToken old = existing.get();
    if (old.isRevoked()) {
      throw new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    if (old.getExpiresAt().isBefore(Instant.now())) {
      throw new DiscodeitException(ErrorCode.EXPIRED_TOKEN);
    }
    if (!old.getUserId().equals(userId)) {
      throw new DiscodeitException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // revoke old and create new
    old.setRevoked(true);
    UUID newJti = UUID.randomUUID();
    old.setReplacedBy(newJti);
    repository.save(old);

    String newToken = jwtTokenProvider.generateRefreshToken(userId, newJti);
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(jwtTokenProvider.getRefreshTokenValiditySeconds());
    RefreshToken newEntity = new RefreshToken(newJti, userId, now, expiresAt, false, null);
    repository.save(newEntity);

    return newToken;
  }

  @Transactional
  public void revoke(UUID jti) {
    repository.findById(jti).ifPresent(token -> {
      token.setRevoked(true);
      repository.save(token);
    });
  }

}

