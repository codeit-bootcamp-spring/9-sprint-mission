package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelCreateRequest(
    String name,
    ChannelType type
) {
}
