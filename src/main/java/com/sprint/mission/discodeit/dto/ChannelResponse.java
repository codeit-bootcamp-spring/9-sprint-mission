package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String name,
        String description,
        ChannelType type,
        Instant lastMessageAt,
        List<UUID> participantIds,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChannelResponse from(Channel channel, Instant latestMessageAt, List<UUID> participantUserIds) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getChannelType(),
                latestMessageAt,
                participantUserIds != null ? List.copyOf(participantUserIds) : Collections.emptyList(),
                channel.getCreatedAt(),
                channel.getUpdatedAt()
        );
    }
}
