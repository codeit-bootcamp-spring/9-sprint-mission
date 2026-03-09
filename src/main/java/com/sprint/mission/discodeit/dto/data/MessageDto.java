package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID channelId,
    UserDto author,
    String content,
    List<BinaryContentDto> attachments,
    Instant createdAt,
    Instant updatedAt
) {}