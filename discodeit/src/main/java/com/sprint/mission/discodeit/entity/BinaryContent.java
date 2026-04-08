package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity; // BaseEntity 상속이 필요할 수 있습니다.
import jakarta.persistence.Column;
import jakarta.persistence.Entity; // JPA 엔티티로 등록해야 합니다.
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity // DB 테이블과 연결하기 위해 추가
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

  private String fileName;
  private Long size;
  private String contentType;

  // @Column(name = "bytes")

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}