package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity implements Serializable {
  @Column(name = "receiver_id", nullable = false)
  private UUID receiverId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "content", nullable = false, length = 1000)
  private String content;

  public Notification(UUID receiverId, String title, String content) {
    super();
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }
}
