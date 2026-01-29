package com.sprint.mission.discodeit.DTO.ChannelService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FindChannelResponse(
        UUID id,
        String name,
        String description,
        Instant lastMessageTime,
        List<UUID> userList
) { }
