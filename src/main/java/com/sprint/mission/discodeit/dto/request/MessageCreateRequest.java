package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,
        UUID authorId,
        String content,
        List<UUID> attachmentIds
) {}
