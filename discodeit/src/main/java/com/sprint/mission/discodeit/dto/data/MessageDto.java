package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UUID authorId,
    String authorUsername,
    List<BinaryContentDto> attachments
) {
  public static MessageDto from(Message message) {
    List<BinaryContentDto> attachmentDtos = message.getAttachments().stream()
        .map(BinaryContentDto::from)
        .collect(Collectors.toList());

    return new MessageDto(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        message.getAuthor().getId(),
        message.getAuthor().getUsername(), // authorName
        attachmentDtos
    );
  }
}