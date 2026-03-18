package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "read_statuses", uniqueConstraints = {
    @UniqueConstraint(name = "uk_read_status_user_channel", columnNames = {"user_id", "channel_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @Column(nullable = false)
  private Instant lastReadAt;

  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void update(Instant newLastReadAt) {
    if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
      this.lastReadAt = newLastReadAt;
    }
  }

  // UUID -> User,Channel , JPA가 다른 테이블과 연결된 외래키라고 인식
  // @ManyToOne 통해 다대일 관계 설정, 한명의 유저는 여러 채널의 읽기 상태를 가질 수 있음
  // fetch = FetchType.Lazy 로 읽기상태를 조회할 때 연관된 유저나 채널 정보를 불필요하게 미리 가져오지않게 설정


}
