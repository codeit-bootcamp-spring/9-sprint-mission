package com.sprint.mission.discodeit.dto;

public class BinaryContentCreateRequestDto {

    private final String fileName;
    private final byte[] bytes;

    public BinaryContentCreateRequestDto(String fileName, byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
