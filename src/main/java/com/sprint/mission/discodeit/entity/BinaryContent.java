package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final byte[] data;
    private final String contentType;
    private final long size;
    private final Instant createdAt;

    public BinaryContent(byte[] data, String contentType) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("파일 데이터는 필수입니다.");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType은 필수입니다.");
        }

        this.id = UUID.randomUUID();
        this.data = data;
        this.contentType = contentType;
        this.size = data.length;
        this.createdAt = Instant.now();
    }
}
