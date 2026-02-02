package com.sprint.mission.discodeit.DTO.ChannelService;

import java.util.UUID;

public record UpdateChannelRequest(
        UUID id,
        String name,
        String description
) {
}
