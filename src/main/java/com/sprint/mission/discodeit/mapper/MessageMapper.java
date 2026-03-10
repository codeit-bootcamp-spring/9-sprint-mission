package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

  public MessageDto toDto(Message message) {
    List<BinaryContentDto> attachments = message.getAttachments().stream()
        .map(attachment -> new BinaryContentDto(
            attachment.getId(),
            attachment.getFileName(),
            attachment.getSize(),
            attachment.getContentType()
        ))
        .toList();

    return new MessageDto(
        message.getId(),
        message.getChannel().getId(),
        message.getAuthor().getId(),
        message.getContent(),
        attachments,
        message.getCreatedAt()
    );
  }
}
