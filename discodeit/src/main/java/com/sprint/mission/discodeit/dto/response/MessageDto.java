package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UserDto author, // authorId(UUID) -> UserDto로 변경
    List<BinaryContentDto> attachments // attachmentIds -> List<BinaryContentDto>로 변경
) {

}