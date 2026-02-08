package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class BinaryContent implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String filename;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(String filename, String contentType, byte[] data) {
        this.id = UUID.randomUUID();
        this.filename = filename;
        this.data = data;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }



    public byte[] getData() {
        return data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
