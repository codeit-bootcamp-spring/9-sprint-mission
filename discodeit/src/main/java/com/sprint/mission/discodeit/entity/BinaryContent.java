package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class BinaryContent extends BaseEntity {

    private byte[] bytes;
    private String contentType;
    private String fileName;
    private Long fileSize;

    public BinaryContent(byte[] bytes, String contentType, String fileName, Long fileSize) {
        super();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}