package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt,
    boolean notificationEnabled
) {

  public ReadStatusResponse(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
    this(id, userId, channelId, lastReadAt, false);
  }
}
