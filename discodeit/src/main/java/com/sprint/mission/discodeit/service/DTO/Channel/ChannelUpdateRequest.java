package com.sprint.mission.discodeit.service.DTO.Channel;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId,
        String name,
        String description
) {
}
