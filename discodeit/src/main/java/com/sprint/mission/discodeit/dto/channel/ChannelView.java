package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelView(
        UUID id,
        ChannelType type,
        String name,
        String description,
        UUID ownerId,
        Instant createdAt,
        Instant updatedAt,

        Instant lastMessageAt,
        List<UUID> participantUserIds
) {
}
