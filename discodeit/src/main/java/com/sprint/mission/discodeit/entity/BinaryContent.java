package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseUpdatableEntity {  // BaseEntity → BaseUpdatableEntity 로 변경!

  private String fileName;
  private Long size;
  private String contentType;

  @Enumerated(EnumType.STRING)   // DB에 "PROCESSING", "SUCCESS", "FAIL" 문자열로 저장
  @Column(nullable = false)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.status = BinaryContentStatus.PROCESSING;  // 처음엔 항상 업로드 중으로 시작
  }

  // 상태 바꿔주는 메서드
  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}