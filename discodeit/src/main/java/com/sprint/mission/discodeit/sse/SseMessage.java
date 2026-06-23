package com.sprint.mission.discodeit.sse;

import java.util.UUID;
import lombok.Getter;

@Getter
public class SseMessage {
  private final UUID id;
  private final String eventName;
  private final Object data;

  public SseMessage(UUID id, String eventName, Object data) {
    this.id = id;
    this.eventName = eventName;
    this.data = data;
  }
}