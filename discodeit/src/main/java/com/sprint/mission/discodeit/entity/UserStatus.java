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

  // 내부 관리용 상태값 (v1.1 DTO에는 노출되지 않음)
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
    // 마지막 활동 시간이 갱신되면 논리적으로 ONLINE 상태로 간주 가능
    this.status = "ONLINE";
  }

  // UserDto의 'online' 필드 계산 로직
  public boolean isOnline() {
    if (this.lastActiveAt == null) {
      return false;
    }
    // 예: 마지막 활동이 5분 이내면 온라인으로 판단
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300));
  }
}