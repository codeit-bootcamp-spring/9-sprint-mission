package com.sprint.mission.discodeit.service.DTO.Message;

import com.sprint.mission.discodeit.service.DTO.AttachmentCreatRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<AttachmentCreatRequest> attachments
) {
}
