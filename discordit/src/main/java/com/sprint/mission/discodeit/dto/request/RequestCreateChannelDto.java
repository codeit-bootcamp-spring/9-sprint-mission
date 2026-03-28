package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public record RequestCreateChannelDto(
    String name, String description, ChannelType type,
    List<UUID> participantIds
) {

  public Channel toEntity() {
    return new Channel(name, description, type, participantIds);
  }
}
