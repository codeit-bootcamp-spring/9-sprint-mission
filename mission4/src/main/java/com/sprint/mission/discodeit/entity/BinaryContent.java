package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class BinaryContent extends BaseUpdatableEntity {

  private String fileName;
  private Long size;
  private String contentType;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  @Builder.Default
  private BinaryContentStatus status = BinaryContentStatus.PROCESSING;


  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}
