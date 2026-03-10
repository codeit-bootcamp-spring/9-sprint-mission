package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

  private final UserMapper userMapper;

  public MessageMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

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
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        userMapper.toDto(message.getAuthor()),
        attachments
    );
  }
}
