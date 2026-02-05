package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId,
        ChannelUpdateParams params
) {
    public record ChannelUpdateParams(
            String name,
            String description
    ) {
    }
}
