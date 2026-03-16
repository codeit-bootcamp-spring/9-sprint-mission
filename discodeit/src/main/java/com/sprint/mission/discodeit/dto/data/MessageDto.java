package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserDto author, // authorId -> author 객체로 변경
        List<BinaryContentDto> attachments // attachmentIds -> attachments 객체 배열로 변경
) {
    public MessageDto {
        if (attachments == null) {
            attachments = java.util.List.of();
        }
    }
}