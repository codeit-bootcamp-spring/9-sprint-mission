package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType; // [추가]
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelDto {
    record CreatePublicRequest(
            String name,
            String description,
            ChannelType type
    ) {}

    record CreatePrivateRequest(
            String name,
            String description,
            ChannelType type,
            List<UUID> participantUserIds
    ) {}

    record Response(
            UUID id,
            String name,
            String description,
            String type,
            Instant lastMessageAt,
            List<UUID> participantUserIds
    ) {}
}