package com.sprint.mission.discodeit.dto.binaryContent;

public record CreateBinaryContentRequest(
        String fileName,
        String contentType,
        byte[] data
) {
}
