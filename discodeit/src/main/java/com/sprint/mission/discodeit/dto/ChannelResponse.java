package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String name,
        String description,
        String type,
        Instant lastMessageAt,
        List<UUID> participantUserIds,
        boolean isPrivate
) {}