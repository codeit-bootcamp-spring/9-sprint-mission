package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private final UUID id;
    private final Instant createdAt;

    private final UUID ownerUserId;     // 프로필 이미지용
    private final UUID messageId;       // 메시지 첨부용

    private final String filename;
    private final byte[] data;
    private final String contentType;

    public BinaryContent(
            UUID ownerUserId,
            UUID messageId,
            String filename,
            byte[] data,
            String contentType
    ) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.ownerUserId = ownerUserId;
        this.messageId = messageId;
        this.filename = filename;
        this.data = data;
        this.contentType = contentType;
    }

}