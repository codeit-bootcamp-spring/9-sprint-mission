package com.sprint.mission.discodeit.DTO.ChannelService.Request;

import java.util.List;
import java.util.UUID;

public record CreatePrivateChRequest(
        List<UUID> memberList
) {
}
