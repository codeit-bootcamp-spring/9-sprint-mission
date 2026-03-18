package com.sprint.mission.discodeit.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity // 이 클래스는 데이터베이스 테이블이랑 연결될 거야
@Table(name = "user_statuses") //연결될 실제 테이블 이름
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //jpa가 사용할 빈 생성자
public class UserStatus extends BaseUpdatableEntity {
  //BaseUpdatableEntity 상속: id,createdAt,updatedAt 물려받음

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true) //erd의 uk,nn 제약조건
  @JsonBackReference //User와 서로 참조할 때 무한 루프에 빠지지 않도록 막아주는 방패 역할
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  //생성자: BasicUserService에서 객체를 새로 만들 때 사용할 수 있도록 User를 받게 수정
  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
    //id와 createdAt은 부모 클래스에서 자동으로 만들어주기 때문에 생략해도 ㄱㅊ
  }

  public void update(Instant lastActiveAt) {
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
