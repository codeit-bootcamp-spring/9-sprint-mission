package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public class MessageDTO{

    public record CreateMessageDTO (
            String text,
            UUID channelId,
            UUID authorId,
            List<UUID> attachmentIds
    ) { }

    public record UpdateMessageDTO (
            String text
    ) { }
}
