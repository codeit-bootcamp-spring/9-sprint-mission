package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor
public class UserStatus extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false)
  private String status;

  private Instant lastActiveAt;

  public UserStatus(User user) {
    this.user = user;
    this.status = "OFFLINE";
    this.lastActiveAt = Instant.now();
  }

  public void updateLastActiveAt(Instant lastActiveAt) {
    this.lastActiveAt = lastActiveAt;
    this.status = "ONLINE";
  }

  public boolean isOnline() {
    if (this.lastActiveAt == null) {
      return false;
    }
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}