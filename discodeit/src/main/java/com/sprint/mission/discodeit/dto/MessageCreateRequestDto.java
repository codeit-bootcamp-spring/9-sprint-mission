package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequestDto;

import java.util.List;
import java.util.UUID;

public class MessageCreateRequestDto {

    private final String content;
    private final UUID channelId;
    private final UUID authorId;

    // 첨부파일 목록
    private final List<BinaryContentCreateRequestDto> attachments;

    public MessageCreateRequestDto(
            String content,
            UUID channelId,
            UUID authorId,
            List<BinaryContentCreateRequestDto> attachments
    ) {
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachments = attachments;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public List<BinaryContentCreateRequestDto> getAttachments() {
        return attachments;
    }
}
