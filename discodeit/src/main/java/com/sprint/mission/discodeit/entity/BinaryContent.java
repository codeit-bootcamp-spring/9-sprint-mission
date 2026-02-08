package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public final class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String filename;
    private final String contentType;
    private final byte[] bytes;
    private final Instant createdAt;

    public BinaryContent (UUID id, String filename, String contentType, byte[] bytes) {
        if (id == null) throw new IllegalArgumentException("id must not be null");
        if (bytes == null) throw new IllegalArgumentException("bytes must not be null");

        this.id = UUID.randomUUID();
        this.filename = filename;
        this.contentType = contentType;
        this.bytes = bytes;
        this.createdAt = Instant.now();
    }
}
