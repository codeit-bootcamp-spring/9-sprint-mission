package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*; // Entity, Id, Table, OneToOne, JoinColumn 등을 위해 추가
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private UUID id;
  private Instant createdAt;
  private Instant updatedAt;


  @OneToOne
  @JoinColumn(name = "user_id") // DB 테이블의 컬럼 이름입니다.
  private User user;
  private Instant lastActiveAt;

  // 2. 생성자도 User 객체를 받도록 수정합니다.
  public UserStatus(User user, Instant lastActiveAt) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
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

  public Boolean isOnline() {
    if (lastActiveAt == null) {
      return false;
    }
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}