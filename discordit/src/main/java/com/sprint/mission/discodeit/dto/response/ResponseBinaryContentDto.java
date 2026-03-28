package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ResponseBinaryContentDto(
        UUID id,
        Instant createAt,
        String fileName,
        String fileExtension,
        String contentType,
        byte[] bytes
) {
    @Override
    public String toString() {
        return "\n        BinaryContent ID : " + id
                + "\n        fileName : " + fileName + "." + fileExtension + "\n";
    }
}
