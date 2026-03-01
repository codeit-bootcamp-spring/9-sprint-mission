package com.sprint.mission.discordit.dto.data;

import com.sprint.mission.discordit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    List<UUID> participantIds,
    Instant lastMessageAt
) {

}
