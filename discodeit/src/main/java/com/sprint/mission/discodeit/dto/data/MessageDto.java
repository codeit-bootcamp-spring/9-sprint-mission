package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author,
    List<BinaryContentDto> attachments
) {

  // 🛡️ [방패 추가] null이 들어오면 빈 리스트로 바꿔치기합니다.
  public MessageDto {
    if (attachments == null) {
      attachments = List.of();
    }
  }
}