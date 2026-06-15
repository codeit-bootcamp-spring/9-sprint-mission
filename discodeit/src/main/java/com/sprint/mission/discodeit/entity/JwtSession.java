package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "jwt_sessions")
public class JwtSession extends BaseUpdatableEntity {

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false, unique = true, length = 512)
  private String accessToken;

  @Column(nullable = false, unique = true, length = 512)
  private String refreshToken;

  @Column(nullable = false)
  private Instant expirationTime;

  public JwtSession(UUID userId, String accessToken,
      String refreshToken, Instant expirationTime) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expirationTime = expirationTime;
  }
}