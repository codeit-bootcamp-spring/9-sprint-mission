package com.sprint.mission.discodeit.DTO.MessageService.Request;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest(UUID authorId,
                                   UUID channelId,
                                   String content,
                                   List<UUID> attachmentIds) {
}
