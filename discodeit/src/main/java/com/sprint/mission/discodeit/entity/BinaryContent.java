package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BinaryContent extends BaseEntity {
    private static final long serialVersionUID = 1L;
    private final String fileName;
    private final Long size;
    private final String contentType;
    public byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] data){
        super();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = data;
        this.size = (long) data.length;

        System.out.println("BinaryContent 생성 - " + this.toString());
    }
}
