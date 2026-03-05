package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;
  //
  private User user;
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  public void update(Instant lastActiveAt) {
    boolean anyValueUpdated = false;

    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

  public boolean isOnline() {

    if (lastActiveAt == null) {
      return false;
    }

    Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(fiveMinutesAgo);
  }
}