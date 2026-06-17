package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status
) {
  // 테스트 코드 및 하위 호환성을 위한 오버로딩 생성자
  public BinaryContentDto(UUID id, String fileName, Long size, String contentType) {
    this(id, fileName, size, contentType, null);
  }
}
