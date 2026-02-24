package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class BinaryContent extends BaseEntity {

  private byte[] bytes;
  private String contentType;
  private String fileName;
  private Long size;

  public BinaryContent(byte[] bytes, String contentType, String fileName, Long size) {
    super();
    this.bytes = bytes;
    this.contentType = contentType;
    this.fileName = fileName;
    this.size = size;
  }
}