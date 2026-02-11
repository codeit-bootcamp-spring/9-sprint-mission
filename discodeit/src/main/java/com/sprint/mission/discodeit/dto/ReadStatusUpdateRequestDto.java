package com.sprint.mission.discodeit.dto;

import java.time.Instant;

public class ReadStatusUpdateRequestDto {

    private final Instant lastReadAt;

    public ReadStatusUpdateRequestDto(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    public Instant getLastReadAt() {
        return lastReadAt;
    }
}
