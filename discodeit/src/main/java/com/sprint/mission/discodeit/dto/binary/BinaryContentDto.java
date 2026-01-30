package com.sprint.mission.discodeit.dto.binary;

public record BinaryContentDto (
        String filename,
        String contentType,
        byte[] data
    )
{
}
