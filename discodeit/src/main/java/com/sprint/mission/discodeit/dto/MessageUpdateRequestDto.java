package com.sprint.mission.discodeit.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageUpdateRequestDto {

    private final UUID messageId;
    private final String content;

    public MessageUpdateRequestDto(UUID messageId, String content) {
        this.messageId = messageId;
        this.content = content;
    }
}
