package com.sprint.mission.discodeit.event;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public record SseSendRequiredEvent(
    Set<UUID> receiverIds,
    String eventName,
    Object data
) {

  public SseSendRequiredEvent(Collection<UUID> receiverIds, String eventName, Object data) {
    this(Set.copyOf(receiverIds), eventName, data);
  }
}
