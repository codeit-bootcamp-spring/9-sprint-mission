package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public record ChannelCreateRequestDto (
    ChannelType type,
    String name,
    String description
    ){
}
