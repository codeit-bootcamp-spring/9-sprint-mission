package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private final UUID id;
    private final Instant createAt;
    private final String fileName;
    private final String fileExtension;
    private final AttachmentType type;
    private final byte[] bytes;

    public BinaryContent(AttachmentType type, String fileName, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createAt = Instant.now();
        this.fileName = fileName.split("\\.")[0];
        this.fileExtension = fileName.split("\\.")[1];
        this.bytes = bytes;
        this.type = type;
    }

    @Override
    public String toString() {
        return "\n        BinaryContent ID : " + id
                + "\n        fileName : " + fileName + "." + fileExtension + "\n";
    }
}
