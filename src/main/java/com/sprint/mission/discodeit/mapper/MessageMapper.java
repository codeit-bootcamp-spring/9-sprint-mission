package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

  public MessageDto toDto(Message message) {
    return new MessageDto(
        message.getId(),
        message.getChannelId(),
        message.getSenderId(),
        message.getContent(),
        message.getAttachmentIds(),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }
}