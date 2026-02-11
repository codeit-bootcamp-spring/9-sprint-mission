package com.sprint.mission.discodeit.dto.binary;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String filename,
        String contentType,
        byte[] data
) {

    public BinaryContent toEntity() {
        return new BinaryContent( filename, contentType, data);
    }
}
