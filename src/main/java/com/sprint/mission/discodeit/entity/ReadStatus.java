package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "read_statuses",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "channel_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", columnDefinition = "uuid")
  private User user;
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "channel_id", columnDefinition = "uuid")
  private Channel channel;
  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private Instant lastReadAt;
  @Column(nullable = false)
  private boolean notificationEnabled = false;

  public ReadStatus(User user, Channel channel, Instant lastReadAt, boolean notificationEnabled) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
    this.notificationEnabled = notificationEnabled;
  }

  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
  }

  public void updateNotificationEnabled(Boolean enabled) {
    if (enabled != null) {
      this.notificationEnabled = enabled;
    }
  }

  public boolean isNotificationEnabled() {
    return this.notificationEnabled;
  }
}