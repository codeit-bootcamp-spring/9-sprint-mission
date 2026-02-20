package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,
        UUID senderId,
        Params params
) {
    public record Params(
            String content,
            List<AttachmentParams> attachments
    ) {
    }

    public record AttachmentParams(
            byte[] bytes,
            String contentType,
            String filename
    ) {
    }
}
