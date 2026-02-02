package com.sprint.mission.discodeit.DTO.ChannelService;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChRequest(
        List<UUID> memberList
) {
}
