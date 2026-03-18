package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class MessageMapper {

  public MessageDto toDto(Message message) {
    if (message == null) {
      return null;
    }

    return new MessageDto(
        message.getId(),
        message.getContent(),
        message.getChannel().getId(), //Channel 객체에서 Id만
        message.getAuthor().getId(),
        message.getAttachmentIds(),
        message.getCreatedAt(),
        message.getUpdatedAt()
    );
  }

}
