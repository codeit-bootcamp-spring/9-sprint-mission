package com.sprint.mission.discodeit.dto.binary;

public class BinaryContentCreateRequestDto {

    private final String filename;
    private final String contentType;
    private final byte[] data;

    public BinaryContentCreateRequestDto(String filename, String contentType, byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
