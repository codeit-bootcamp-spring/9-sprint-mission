package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentIds,
    Instant createdAt,
    Instant updatedAt
) {

}