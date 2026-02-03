package com.sprint.mission.discodeit.service.DTO.Channel;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreatRequest(
        List<UUID> userIds

) {
}
