package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record PublicChannelCreateRequest(
        String name,
        UUID ownerId,
        String description
) {
}
