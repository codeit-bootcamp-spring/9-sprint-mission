package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID receiverId,
    String content,
    Instant createdAt
) {
}
