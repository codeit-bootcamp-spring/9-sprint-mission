package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Getter
@EntityScan
public class UserStatus implements Serializable {

  private final UUID id;
  private final UUID userId;
  private final String username;
  private final Instant createAt;
  private Instant updateAt;

  public UserStatus(UUID userId, String username) {
    this.id = UUID.randomUUID();
    Instant now = Instant.now();
    this.userId = userId;
    this.username = username;
    this.updateAt = now;
    this.createAt = now;
  }

  public void lastAccessTimeUpdater() {
    updateAt = Instant.now();
  }

  public void lastAccessTimeUpdater(Instant time) {
    updateAt = time;
  }

  public boolean isOnline() {
    try {
      Duration duration = Duration.between(this.getUpdateAt(), Instant.now());
      return duration.toMinutes() <= 5;
    } catch (Exception ignore) {
      return false;
    }
  }
}
