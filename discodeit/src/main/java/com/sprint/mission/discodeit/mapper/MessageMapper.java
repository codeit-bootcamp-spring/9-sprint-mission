package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageAttachment;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserMapper.class})
public interface MessageMapper {

  @Mapping(target = "channelId", source = "channel.id")
  @Mapping(target = "author", source = "author")
  @Mapping(target = "attachments", source = "attachments", qualifiedByName = "mapAttachments")
  MessageResponse toResponse(Message message);

  @Named("mapAttachments")
  default List<BinaryContentResponse> mapAttachments(List<MessageAttachment> attachments) {
    if (attachments == null) {
      return List.of();
    }

    return attachments.stream()
        .map(MessageAttachment::getAttachment)
        .map(this::mapBinaryContent)
        .toList();
  }

  BinaryContentResponse mapBinaryContent(
      BinaryContent binaryContent);
}
