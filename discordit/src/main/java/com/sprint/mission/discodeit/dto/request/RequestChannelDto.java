package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestChannelDto {

  private UUID id;
  private String name;
  private String description;
  private ChannelType type;
  private UUID createUserId;
  private List<UUID> participantIds;

  public Channel toEntity() {
    return new Channel(name, description, type, participantIds);
  }

}
