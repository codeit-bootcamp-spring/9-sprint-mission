package com.sprint.mission.discodeit.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private final UUID id;
    private final String filename;
    private final String contentType;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(
            UUID id,
            String filename,
            String contentType,
            byte[] data
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = Instant.now();
    }
}

