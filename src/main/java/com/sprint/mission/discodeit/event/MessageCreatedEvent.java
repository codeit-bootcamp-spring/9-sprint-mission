package com.sprint.mission.discodeit.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageCreatedEvent {
  private final UUID messageId;
  private final UUID channelId;
  private final UUID authorId;
  private final String content;

  public MessageCreatedEvent(UUID messageId, UUID channelId, UUID authorId, String content) {
    this.messageId = messageId;
    this.channelId = channelId;
    this.authorId = authorId;
    this.content = content;
  }
}

