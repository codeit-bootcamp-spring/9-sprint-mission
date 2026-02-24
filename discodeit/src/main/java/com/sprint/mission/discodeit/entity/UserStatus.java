package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class UserStatus extends BaseEntity {

  private UUID userId;
  private String status;
  private Instant lastActiveAt;

  public UserStatus(UUID userId) {
    super();
    this.userId = userId;
    this.status = "OFFLINE";
    this.lastActiveAt = Instant.now();
  }

  public void updateStatus(String status) {
    if (status == null || status.isBlank()) {
      throw new IllegalArgumentException("상태값은 비어있을 수 없습니다.");
    }
    this.status = status;
    this.updateLastActiveAt();
  }

  public boolean isOnline() {
    if (this.lastActiveAt == null) {
      return false;
    }
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }

  public void updateLastActiveAt() {
    this.lastActiveAt = Instant.now();
    this.recordUpdate();
  }
}