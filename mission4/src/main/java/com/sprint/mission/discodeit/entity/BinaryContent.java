package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BinaryContent extends BaseEntity {

  private String fileName;
  private Long size;
  private String contentType;


  public BinaryContent(String fileName, Long size, String contentType) {

    //
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}
