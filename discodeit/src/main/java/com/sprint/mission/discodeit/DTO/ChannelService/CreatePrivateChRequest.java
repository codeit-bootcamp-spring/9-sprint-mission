package com.sprint.mission.discodeit.DTO.ChannelService;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public record CreatePrivateChRequest(
        List<User> memberList
) {
}
