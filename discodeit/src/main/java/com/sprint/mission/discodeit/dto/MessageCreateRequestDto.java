package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public class MessageCreateRequestDto {

    private final UUID channelId;
    private final UUID authorId;
    private final String content;

    private final List<BinaryContentCreateRequestDto> attachments;

    public MessageCreateRequestDto(
            UUID channelId,
            UUID authorId,
            String content,
            List<BinaryContentCreateRequestDto> attachments
    ) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.attachments = attachments;
    }

}
