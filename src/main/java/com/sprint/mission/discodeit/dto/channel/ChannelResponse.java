package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChannelResponse {
    private UUID id;
    private String name;
    private String description;
    private ChannelType type;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastMessageAt;
    private List<UUID> participantUserIds;
}
