package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    byte[] bytes,
    long size,
    String contentType
) {
    public static BinaryContentResponse from(BinaryContent binaryContent) {
        return new BinaryContentResponse(
            binaryContent.getId(),
            binaryContent.getFileName(),   // 누락된 필드 추가
            binaryContent.getBytes(),      // 누락된 필드 추가
            binaryContent.getSize(),
            binaryContent.getContentType()
        );
    }
}
