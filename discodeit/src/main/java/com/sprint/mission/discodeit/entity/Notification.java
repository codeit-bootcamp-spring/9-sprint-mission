package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
  @Column(nullable = false)
  UUID receiverId;
  @Column(nullable = false)
  String title;
  @Column(nullable = false)
  String content;

  public Notification(UUID receiverId, String title, String content) {
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }
}
