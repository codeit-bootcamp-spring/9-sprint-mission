package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@EntityScan
public class BinaryContent implements Serializable {
    private final UUID id;
    private final Instant createAt;
    private final String fileName;
    private final String fileExtension;
    private final String type;
    private final byte[] bytes;

    public BinaryContent(String type, String fileName, byte[] bytes) {
        String[] nameParts = fileName.split("\\.");
        this.fileName = nameParts[0];
        this.fileExtension = nameParts.length > 1 ? nameParts[1] : "";
        this.id = UUID.randomUUID();
        this.createAt = Instant.now();
        this.bytes = bytes;
        this.type = type;
    }
}
