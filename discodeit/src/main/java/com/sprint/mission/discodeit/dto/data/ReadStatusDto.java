package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt,
    boolean notificationEnabled
) {

  public ReadStatusDto(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
    this(id, userId, channelId, lastReadAt, false);
  }
}
