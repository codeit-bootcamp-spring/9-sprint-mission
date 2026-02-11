package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends BaseEntity {

    private final String filename;
    private final String contentType;
    private final long size;
    private final byte[] bytes;

    public BinaryContent(String filename, String contentType, byte[] bytes) {
        super();
        this.filename = filename;
        this.contentType = contentType;
        this.bytes = (bytes == null) ? new byte[0] : bytes;
        this.size = this.bytes.length;
    }
}


