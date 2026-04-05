package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserResponse author,
    List<BinaryContentResponse> attachments
) {

}

