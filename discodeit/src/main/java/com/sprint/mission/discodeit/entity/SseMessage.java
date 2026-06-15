package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import lombok.Getter;

@Getter
public class SseMessage {

  private final UUID id;
  private final String name;
  private final Object data;
  private final UUID receiverId;

  public SseMessage(String name, Object data, UUID receiverId) {
    this.id = UUID.randomUUID();
    this.name = name;
    this.data = data;
    this.receiverId = receiverId;
  }
}
