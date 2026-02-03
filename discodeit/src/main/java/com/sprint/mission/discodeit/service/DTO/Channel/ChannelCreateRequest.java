package com.sprint.mission.discodeit.service.DTO.Channel;

import java.util.UUID;

public record ChannelCreateRequest(
        UUID channelId,
        String name,
        String description
) {
}
