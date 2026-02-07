package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class ReadStatusCreateRequestDto {

    private final UUID userId;
    private final UUID channelId;

    public ReadStatusCreateRequestDto(UUID userId, UUID channelId) {
        this.userId = userId;
        this.channelId = channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }
}
