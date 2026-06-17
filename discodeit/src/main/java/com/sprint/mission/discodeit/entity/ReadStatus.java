package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "read_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne
  private User user;

  @ManyToOne
  private Channel channel;

  private Instant lastReadAt;

  @Column(nullable = false)
  private boolean notificationEnabled;  // 알림 여부 추가

  public ReadStatus(User user, Channel channel, Instant lastReadAt, boolean notificationEnabled) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
    this.notificationEnabled = notificationEnabled;
  }

  public void update(Instant newLastReadAt, Boolean newNotificationEnabled) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
    if (newNotificationEnabled != null) {
      this.notificationEnabled = newNotificationEnabled;
    }
  }
}