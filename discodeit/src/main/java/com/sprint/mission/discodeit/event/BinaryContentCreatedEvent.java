package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record BinaryContentCreatedEvent(
    UUID binaryContentId,  // 어떤 파일인지 ID
    byte[] bytes           // 실제 파일 데이터
) {

}