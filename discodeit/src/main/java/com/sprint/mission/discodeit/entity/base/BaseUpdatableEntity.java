package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate // 데이터 내용이 바뀔 때마다 시간을 자동으로 업데이트 해줘
  @Column(columnDefinition = "timestamp with time zone")
  private Instant updatedAt;
}