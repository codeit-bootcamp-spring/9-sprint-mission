package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    String name,
    String description,
    ChannelType channelType,
    Instant lastMessageAt,
    List<UserDto> participants,
    Instant createdAt,
    Instant updatedAt
) {}