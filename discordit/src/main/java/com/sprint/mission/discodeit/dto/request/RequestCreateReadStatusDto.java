package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record RequestCreateReadStatusDto(
        UUID userId,
        List<UUID> channelIds
) {
}
