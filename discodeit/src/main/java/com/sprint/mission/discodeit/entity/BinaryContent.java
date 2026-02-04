package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    UUID id;
    Long createdAt;
    String url;

    public BinaryContent(String url, UUID ownerId) {
        this.createdAt = Instant.now().getEpochSecond();
        this.url = url;
        this.id = UUID.randomUUID();
    }

    
    public void updateLastJoinAt() {
    }
}
