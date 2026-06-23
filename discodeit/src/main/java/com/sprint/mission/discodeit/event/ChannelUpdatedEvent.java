package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

public record ChannelUpdatedEvent(
    String updatedType,
    ChannelDto channelDto
) {

}
