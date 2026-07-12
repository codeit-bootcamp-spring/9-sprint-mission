package com.sprint.mission.discodeit.entity.event;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import java.util.List;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    UUID authorId,
    String content,
    List<UUID> attachmentIds

) {

  public static MessageCreatedEvent from(MessageDto dto) {
    return new MessageCreatedEvent(
        dto.id(),
        dto.channelId(),
        dto.author().id(),
        dto.content(),
        dto.attachments() == null ? List.of() : dto.attachments().stream().map(a -> a.id()).toList()
    );
  }

}


