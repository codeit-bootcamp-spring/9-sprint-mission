package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    public static final long serialVersionUID = 1L;

    // 공통
    private final UUID id;
    private final Instant createdAt;

    // 데이터
    private final byte[] bytes;
    private final String contentType;
    private final String fileName;
    private final long size;

    public BinaryContent(UUID id, byte[] bytes, String contentType, String fileName) {
        this.id = id;
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = (bytes == null) ? 0 : bytes.length;
    }


}
