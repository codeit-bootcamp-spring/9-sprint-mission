package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "receiver_id", columnDefinition = "uuid", nullable = false)
  private User receiver;

  @Column(nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private NotificationType type;

  @Column(nullable = false, length = 500)
  private String content;

  // 이벤트 발생 원본 리소스 ID (메시지 ID, 유저 ID 등)
  @Column(columnDefinition = "uuid")
  private UUID resourceId;

  public Notification(User receiver, NotificationType type, String content, UUID resourceId) {
    this.receiver = receiver;
    this.type = type;
    this.content = content;
    this.resourceId = resourceId;
  }
}
