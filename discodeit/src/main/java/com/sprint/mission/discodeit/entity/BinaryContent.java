package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 바이너리 컨텐츠 식별자 */
    private final UUID Id;

    private final byte[] data;
    /** MIME 타입 (image/png, application/pdf 등) */
    private final String contentType;
    /** 데이터 크기 (bytes) */
    private long size;
    private final long createdAt;
    private String fileName;

    public BinaryContent(byte[] data, String contentType, String fileName) {
        this.data = data;
        this.contentType = contentType;
        this.size = data.length;
        this.createdAt = Instant.now().toEpochMilli();
        this.fileName = fileName;
        this.Id = UUID.randomUUID();

    }



}
