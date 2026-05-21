package com.sprint.mission.discodeit.security.jwt.store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

  @Id
  private UUID id; // jti

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private Instant issuedAt;

  @Column(nullable = false)
  private Instant expiresAt;

  @Column(nullable = false)
  private boolean revoked = false;

  private UUID replacedBy;

  public RefreshToken() {}

  public RefreshToken(UUID id, UUID userId, Instant issuedAt, Instant expiresAt, boolean revoked, UUID replacedBy) {
    this.id = id;
    this.userId = userId;
    this.issuedAt = issuedAt;
    this.expiresAt = expiresAt;
    this.revoked = revoked;
    this.replacedBy = replacedBy;
  }

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public Instant getIssuedAt() {
    return issuedAt;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public void setRevoked(boolean revoked) {
    this.revoked = revoked;
  }

  public UUID getReplacedBy() {
    return replacedBy;
  }

  public void setReplacedBy(UUID replacedBy) {
    this.replacedBy = replacedBy;
  }
}

