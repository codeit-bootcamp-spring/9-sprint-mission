package com.sprint.mission.discodeit.dto.request;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    String name,
    String description,
    List<UUID> participantIds
) {

  public List<UUID> participantIds() {
    return (participantIds != null) ? participantIds : Collections.emptyList();

  }
}
