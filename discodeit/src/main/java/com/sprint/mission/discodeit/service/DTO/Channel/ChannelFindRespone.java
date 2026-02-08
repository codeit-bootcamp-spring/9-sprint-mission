package com.sprint.mission.discodeit.service.DTO.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindRespone(
        UUID channelId,
        ChannelType type,
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> participantUserIds
) {
}
