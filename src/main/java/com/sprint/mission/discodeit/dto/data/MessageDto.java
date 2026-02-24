package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID channelId,
    UUID authorId,
    String content,
    Instant createdAt
) {}
