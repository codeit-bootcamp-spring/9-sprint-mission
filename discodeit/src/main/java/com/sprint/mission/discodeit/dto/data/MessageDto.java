package com.sprint.mission.discodeit.dto.data;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDto(
    UUID id,
    String content,
    UUID authorId,
    UUID channelId,
    LocalDateTime createdAt
) {

}