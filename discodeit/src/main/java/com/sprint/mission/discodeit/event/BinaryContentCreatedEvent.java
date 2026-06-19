package com.sprint.mission.discodeit.event;

import java.util.Arrays;
import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

  public BinaryContentCreatedEvent {
    // MVP 학습 범위에서는 byte[]를 전달한다. 운영 환경에서는 임시 파일/S3 staging 경로 참조로 전환한다.
    bytes = Arrays.copyOf(bytes, bytes.length);
  }
}
