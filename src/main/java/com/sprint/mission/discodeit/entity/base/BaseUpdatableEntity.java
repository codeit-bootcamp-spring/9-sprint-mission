package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 수정 가능한 엔티티의 기본 클래스.
 * BaseEntity를 확장하여 수정일시를 JPA Auditing으로 자동 관리한다.
 */
@Getter
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column
  private Instant updatedAt;
}
