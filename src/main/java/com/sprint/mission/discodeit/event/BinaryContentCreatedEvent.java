package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

/**
 * BinaryContent 메타 정보가 DB에 저장 완료된 후 발행되는 이벤트.
 * 리스너가 트랜잭션 커밋 이후 실제 바이너리 데이터를 FileSystem/S3에 저장합니다.
 */
public record BinaryContentCreatedEvent(BinaryContent binaryContent, byte[] bytes) {

  public UUID binaryContentId() {
    return binaryContent.getId();
  }
}
