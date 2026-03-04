package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final Long size;
    private final String contentType;
    public byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] data){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = data;
        this.size = (long) data.length;

        System.out.println("BinaryContent 생성 - " + this.toString());
    }
}
