package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import java.util.Collection;
import java.util.UUID;

public record SseChannelChangedEvent(
    String eventName,
    ChannelResponse channel,
    Collection<UUID> receiverIds
) {

  public boolean isBroadcast() {
    return receiverIds == null || receiverIds.isEmpty();
  }
}
